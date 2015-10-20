package org.openurp.edu.course.syllabus.action

import java.io.File
import java.io.IOException
import java.util.Collection
import java.util.Date
import java.util.List
import java.util.Set
import org.apache.commons.io.FileUtils
import org.beangle.commons.collection.CollectUtils
import org.beangle.commons.collection.Order
import org.beangle.commons.dao.query.builder.OqlBuilder
import org.beangle.commons.entity.Entity
import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.functor.Predicate
import org.openurp.base.model.Semester
import org.openurp.edu.base.model.Course
import org.openurp.edu.base.model.Teacher
import org.openurp.edu.eams.course.Syllabus
import org.openurp.edu.eams.course.syllabus.helper.SyllabusGraphHelper
import org.openurp.edu.eams.course.syllabus.service.SyllabusFileService
import org.openurp.edu.eams.system.simpleworkflow.Graph
import org.openurp.edu.eams.system.simpleworkflow.GraphService
import org.openurp.edu.eams.system.simpleworkflow.Stage
import org.openurp.edu.teach.lesson.model.Lesson
import org.openurp.edu.teach.web.action.AbstractTeacherLessonAction
//remove if not needed
import scala.collection.JavaConversions._

class SyllabusTeacherAction extends AbstractTeacherLessonAction {

  protected var graphService: GraphService = _

  protected var syllabusGraphHelper: SyllabusGraphHelper = _

  protected var syllabusFileService: SyllabusFileService = _

  protected override def getEntityName(): String = classOf[Syllabus].getName

  def search(): String = {
    val teacher = getLoginTeacher
    if (null == teacher) {
      return forwardError("error.teacher.notExists")
    }
    put("semester", getSemester)
    val syllabuses = search(getQueryBuilder)
    val graph = syllabusGraphHelper.getGraph
    put("stageMap", graphService.getStageMap(graph, syllabuses))
    put("syllabuses", syllabuses)
    put("targetStages", graphService.getAuditTargetStages(graph))
    forward()
  }

  protected def getQueryBuilder[T <: Entity[_]](): OqlBuilder[T] = {
    val query = OqlBuilder.from(classOf[Syllabus].getName, "syllabus")
    populateConditions(query)
    val stageId = getIntId("stage")
    if (null != stageId) {
      graphService.addCondition(query, stageId)
    }
    query.where("syllabus.course.project = :project", getProject)
    val enabled = getBoolean("enabled")
    if (null != enabled) {
      val now = new Date()
      if (true == enabled) {
        query.where("syllabus.beginOn <= :now and (syllabus.endOn is null or syllabus.endOn >= :now)", 
          now)
      } else {
        query.where("syllabus.beginOn > :now or (syllabus.endOn is not null and syllabus.endOn < :now)", 
          now)
      }
    }
    query.where("syllabus.teacher = :teacher", getLoginTeacher)
    val orderBy = get(Order.ORDER_STR)
    query.orderBy(if (Strings.isBlank(orderBy)) "syllabus.course.code,syllabus.updatedAt desc" else orderBy)
    query.orderBy(Order.parse(get("orderBy")))
    query.limit(getPageLimit)
    query
  }

  def edit(): String = {
    val teacher = getLoginTeacher
    if (null == teacher) {
      return forwardError("error.teacher.notExists")
    }
    val entity = getEntity
    val syllabus = entity.asInstanceOf[Syllabus]
    val graph = syllabusGraphHelper.getGraph
    if (syllabus.isPersisted) {
      if (!graphService.canModify(graph, syllabus)) {
        return redirect("search", "您没有权限修改该审核状态下大纲")
      }
    } else {
      if (graphService.getAllStages(graph).isEmpty) {
        return redirect("search", "您没有权限上传大纲")
      }
      val lessons = entityDao.search(OqlBuilder.from(classOf[Lesson], "lesson").where("lesson.project = :project", 
        getProject)
        .where("lesson.semester = :semester", syllabus.getSemester)
        .where("exists(from lesson.teachers teacher where teacher=:teacher)", teacher))
      CollectUtils.filter(lessons, new Predicate[Lesson]() {

        var exists: Set[Course] = CollectUtils.newHashSet()

        def apply(input: Lesson): java.lang.Boolean = {
          if (exists.contains(input.getCourse)) {
            return false
          } else {
            exists.add(input.getCourse)
            return true
          }
        }
      })
      put("lessons", lessons)
    }
    put(getShortName, entity)
    forward()
  }

  def save(): String = {
    val teacher = getLoginTeacher
    if (null == teacher) {
      return forwardError("error.teacher.notExists")
    }
    val syllabus = populateEntity().asInstanceOf[Syllabus]
    val param = if (syllabus.isPersisted) "&syllabus.id=" + syllabus.getId else "&syllabus.semester.id=" + syllabus.getSemester.getId
    if (syllabus.getBeginOn == null) {
      return redirect("edit", "不允许生效时间为空", param)
    }
    val date = new Date()
    var tmpFile: File = null
    var oldFile: File = null
    var initStage = false
    if (syllabus.isTransient) {
      val lesson = entityDao.get(classOf[Lesson], getLongId("lesson"))
      syllabus.setCourse(lesson.getCourse)
      syllabus.setCreatedAt(date)
      syllabus.setDepartment(lesson.getTeachDepart)
      initStage = true
    } else {
      oldFile = new File(syllabus.getFile.getPathName + syllabus.getFile.getName)
      tmpFile = new File(FileUtils.getTempDirectoryPath + File.separator + syllabus.getFile.getName)
      try {
        FileUtils.copyFile(oldFile, tmpFile)
      } catch {
        case e: IOException => e.printStackTrace()
      }
    }
    syllabus.setSemester(entityDao.get(classOf[Semester], syllabus.getSemester.getId))
    syllabus.setTeacher(teacher)
    syllabus.setUpdatedAt(date)
    syllabus.setUpdatedBy(getUser)
    val msg = syllabusFileService.uploadFile(syllabus, get("fileName", classOf[File]), get("fileNameFileName"))
    if (Strings.isNotBlank(msg)) {
      if (null != tmpFile) {
        FileUtils.deleteQuietly(tmpFile)
      }
      return redirect("edit", msg, param)
    }
    try {
      entityDao.saveOrUpdate(syllabus)
    } catch {
      case e: Exception => {
        if (null != tmpFile) {
          try {
            FileUtils.copyFile(tmpFile, oldFile)
          } catch {
            case e1: IOException => e1.printStackTrace()
          }
        }
        FileUtils.deleteQuietly(new File(syllabus.getFile.getPathName + syllabus.getFile.getName))
        return redirect("edit", "上传失败", param)
      }
    } finally {
      if (null != tmpFile) {
        FileUtils.deleteQuietly(tmpFile)
      }
    }
    if (initStage) {
      try {
        graphService.initStage(syllabusGraphHelper.getGraph, syllabus)
      } catch {
        case e: Exception => return redirect("search", "上传成功,初始化状态失败")
      }
    }
    redirect("search", "上传成功")
  }

  override def innerIndex(): String = {
    val teacher = getLoginTeacher
    if (null == teacher) {
      return forwardError("error.teacher.notExists")
    }
    getSemester
    put("lessonView", getBool("lessonView"))
    put("educations", getProject.getEducations)
    put("stages", graphService.getAllStages(syllabusGraphHelper.getGraph))
    forward()
  }

  override def info(): String = {
    val syllabus = entityDao.get(classOf[Syllabus], getLongId("syllabus"))
    put(getShortName, syllabus)
    put("stageLogs", graphService.getStageLogs(syllabusGraphHelper.getGraph, syllabus))
    forward()
  }

  def downloadSyllabus(): String = {
    try {
      syllabusFileService.downloadSyllabuses(getModels(classOf[Syllabus], getLongIds("syllabus")))
    } catch {
      case e: Exception => return forwardError("下载失败,请联系管理员")
    }
    null
  }

  protected override def removeAndForward(entities: Collection[_]): String = {
    val syllabuses = entities.asInstanceOf[List[Syllabus]]
    val toRemove = CollectUtils.newArrayList()
    val files = CollectUtils.newArrayList()
    val graph = syllabusGraphHelper.getGraph
    for (syllabus <- syllabuses if graphService.canModify(graph, syllabus)) {
      files.add(new File(syllabus.getFile.getPathName + syllabus.getFile.getName))
      toRemove.add(syllabus)
      toRemove.addAll(graphService.getStageLogs(graph, syllabus))
    }
    try {
      for (file <- files if file.exists()) {
        FileUtils.deleteQuietly(file)
      }
      remove(toRemove)
    } catch {
      case e: Exception => return redirect("search", "info.delete.failure")
    }
    redirect("search", if (toRemove.isEmpty) "当前审核状态下没有可以删除的数据" else if (toRemove.size < syllabuses.size) "部分数据删除成功" else "info.remove.success")
  }

  def audit(): String = {
    val syllabuses = entityDao.get(classOf[Syllabus], getLongIds("syllabus"))
    val remark = get("auditRemark")
    val targetStage = entityDao.get(classOf[Stage], getInt("targetStageId"))
    val graph = syllabusGraphHelper.getGraph
    var message = ""
    for (syllabus <- syllabuses) {
      val msg = graphService.setStage(graph, syllabus, targetStage, remark)
      if (Strings.isNotBlank(msg)) {
        message += syllabus.getCourse.getName + "[" + syllabus.getCourse.getCode + 
          "]:" + 
          msg + 
          "<br/>"
      }
    }
    redirect("search", if (Strings.isBlank(message)) "提交成功" else message)
  }

  def setGraphService(graphService: GraphService) {
    this.graphService = graphService
  }

  def setSyllabusGraphHelper(syllabusGraphHelper: SyllabusGraphHelper) {
    this.syllabusGraphHelper = syllabusGraphHelper
  }

  def setSyllabusFileService(syllabusFileService: SyllabusFileService) {
    this.syllabusFileService = syllabusFileService
  }
}
