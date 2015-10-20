package org.openurp.edu.course.syllabus.action

import java.util.Date
import java.util.List
import org.beangle.commons.collection.Order
import org.beangle.commons.dao.query.builder.OqlBuilder
import org.beangle.commons.entity.Entity
import org.beangle.commons.lang.Strings
import org.openurp.base.model.Department
import org.openurp.edu.eams.course.Syllabus
import org.openurp.edu.eams.course.syllabus.helper.SyllabusGraphHelper
import org.openurp.edu.eams.course.syllabus.service.SyllabusFileService
import org.openurp.edu.eams.system.simpleworkflow.Graph
import org.openurp.edu.eams.system.simpleworkflow.GraphService
import org.openurp.edu.eams.system.simpleworkflow.Stage
import org.openurp.edu.web.action.common.SemesterSupportAction

class SyllabusAuditAction extends SemesterSupportAction {

  protected var graphService: GraphService = _

  protected var syllabusGraphHelper: SyllabusGraphHelper = _

  protected var syllabusFileService: SyllabusFileService = _

  def getEntityName(): String = classOf[Syllabus].getName

  protected def indexSetting() {
    put("educations", getEducations)
    put("stages", graphService.getAllStages(syllabusGraphHelper.getGraph))
    put("departments", getTeachDeparts)
  }

  def search(): String = {
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
    val departments = getTeachDeparts
    if (departments.isEmpty) {
      query.where("1=2")
    } else {
      query.where("syllabus.department in (:departments)", departments)
    }
    val orderBy = get(Order.ORDER_STR)
    query.orderBy(if (Strings.isBlank(orderBy)) "syllabus.course.code,syllabus.updatedAt desc" else orderBy)
    query.orderBy(Order.parse(get("orderBy")))
    query.limit(getPageLimit)
    query
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

  def info(): String = {
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
