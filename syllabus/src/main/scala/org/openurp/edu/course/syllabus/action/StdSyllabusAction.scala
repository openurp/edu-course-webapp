package org.openurp.edu.course.syllabus.action

import org.beangle.commons.collection.CollectUtils
import org.beangle.commons.dao.query.builder.OqlBuilder
import org.openurp.base.util.WeekStates
import org.openurp.edu.base.State
import org.openurp.edu.base.model.Student
import org.openurp.edu.eams.course.Syllabus
import org.openurp.edu.eams.course.syllabus.service.SyllabusFileService
import org.openurp.edu.teach.code.school.LessonTag
import org.openurp.edu.teach.lesson.model.Lesson
import org.openurp.edu.teach.lesson.helper.LessonSearchHelper
import org.openurp.edu.teach.lesson.service.CourseLimitService
import org.openurp.edu.teach.lesson.util.CourseActivityDigestor
import org.openurp.edu.textbook.lesson.model.LessonMaterial
import org.openurp.edu.web.action.common.AbstractStudentProjectSupportAction

class StdSyllabusAction extends AbstractStudentProjectSupportAction {

  protected var syllabusFileService: SyllabusFileService = _

  protected var lessonSearchHelper: LessonSearchHelper = _

  protected var courseLimitService: CourseLimitService = _

  def downloadSyllabus(): String = {
    try {
      syllabusFileService.downloadSyllabuses(CollectUtils.newArrayList(entityDao.get(classOf[Syllabus], 
        getLongId("syllabus"))))
    } catch {
      case e: Exception => return forwardError("下载失败,请联系管理员")
    }
    null
  }

  def search(): String = {
    val lessons = entityDao.search(lessonSearchHelper.buildQuery(false).where("lesson.auditStatus = :status", 
      State.ACCEPTED))
    val guapaiStatus = new HashMap[Lesson, Boolean]()
    for (lesson <- lessons) {
      guapaiStatus.put(lesson, false)
      for (tag <- lesson.getTags if tag.getId == LessonTag.PredefinedTags.GUAPAI.getId) {
        guapaiStatus.put(lesson, true)
      }
    }
    val digestor = CourseActivityDigestor.getInstance.setDelimeter("<br>")
    val arrangeInfo = new HashMap[String, String]()
    for (oneTask <- lessons) {
      arrangeInfo.put(oneTask.getId.toString, digestor.digest(getTextResource, oneTask, ":teacher+ :day :units :weeks :room"))
    }
    put("arrangeInfo", arrangeInfo)
    put("guapaiStatus", guapaiStatus)
    put("lessons", lessons)
    put("weekStates", new WeekStates())
    forward()
  }

  def info(): String = {
    val lessonId = getLongId("lesson")
    val lesson = entityDao.get(classOf[Lesson], lessonId)
    put("guapaiTagId", LessonTag.PredefinedTags.GUAPAI.getId)
    put("fakeGender", courseLimitService.extractGender(lesson.getTeachclass))
    put("educationLimit", courseLimitService.xtractEducationLimit(lesson.getTeachclass))
    put("adminclassLimit", courseLimitService.xtractAdminclassLimit(lesson.getTeachclass))
    put("attendDepartLimit", courseLimitService.xtractAttendDepartLimit(lesson.getTeachclass))
    put("stdTypeLimit", courseLimitService.xtractStdTypeLimit(lesson.getTeachclass))
    put("majorLimit", courseLimitService.xtractMajorLimit(lesson.getTeachclass))
    put("directionLimit", courseLimitService.xtractDirectionLimit(lesson.getTeachclass))
    put("lesson", lesson)
    val query = OqlBuilder.from(classOf[LessonMaterial], "book")
    query.where("book.lesson = :lesson", lesson)
    val lessonMaterials = entityDao.search(query)
    if (CollectUtils.isNotEmpty(lessonMaterials)) {
      put("lessonMaterial", lessonMaterials.get(0))
    }
    put("weekStates", new WeekStates())
    val syllabus = syllabusFileService.getLessonSyllabus(lesson)
    if (null != syllabus) {
      put("syllabus", syllabus)
    }
    forward()
  }

  protected def getEntityName(): String = classOf[Lesson].getName

  override def innerIndex(): String = {
    val student = getLoginStudent
    if (null == student) {
      return forwardError("没有权限")
    }
    getSemester
    forward()
  }

  def setSyllabusFileService(syllabusFileService: SyllabusFileService) {
    this.syllabusFileService = syllabusFileService
  }

  def setLessonSearchHelper(lessonSearchHelper: LessonSearchHelper) {
    this.lessonSearchHelper = lessonSearchHelper
  }

  def setCourseLimitService(courseLimitService: CourseLimitService) {
    this.courseLimitService = courseLimitService
  }
}
