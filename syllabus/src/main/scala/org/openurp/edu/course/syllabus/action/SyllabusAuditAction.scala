package org.openurp.edu.course.syllabus.action

import java.io.File
import java.util.Locale
import org.beangle.commons.lang.SystemInfo
import org.beangle.webmvc.api.annotation.param
import org.beangle.webmvc.api.view.Status
import org.beangle.webmvc.api.view.Stream
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.course.syllabus.model.Revision
import org.beangle.data.dao.OqlBuilder
import java.util.Calendar
import org.beangle.commons.collection.Order
import org.openurp.edu.course.syllabus.model.Revision
import org.openurp.base.model.Semester

/**
 * @author xinzhou
 */
class SyllabusAuditAction extends AbstractSyllabusAction[Revision] {

  override def getQueryBuilder(): OqlBuilder[Revision] = {
    //FIXME 根据项目project选择学期
    val semesterQuery = OqlBuilder.from(classOf[Semester], "semester").where(":now between semester.beginOn and semester.endOn", new java.util.Date())
    val semesters = entityDao.search(semesterQuery)

    val builder = OqlBuilder.from(classOf[Revision], "revision")
    if (!semesters.isEmpty) {
      val semester = semesters.head
      builder.where("revision.updatedAt between :begin and :end", semester.beginOn, semester.endOn)
    }
    populateConditions(builder)
    builder.where("revision.syllabus.course.department =:department",getStaff.state.department)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
  }

  def audit(): View = {
    val passed = getBoolean("passed", false)
    val ids = longIds("revision")
    val revisions = entityDao.search(OqlBuilder.from(classOf[Revision], "r").where("r.id in(:ids)", ids))
    revisions.foreach { r =>
      r.passed = passed
    }
    entityDao.saveOrUpdate(revisions)
    redirect("search", "info.save.success")
  }

}