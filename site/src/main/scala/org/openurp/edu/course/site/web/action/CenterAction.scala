package org.openurp.edu.course.site.web.action

import scala.xml.XML
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Numbers
import org.beangle.commons.web.util.HttpUtils
import org.beangle.data.dao.EntityDao
import org.beangle.webmvc.api.action.ActionSupport
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.annotation.param
import org.openurp.edu.base.model.Course
import org.openurp.edu.course.site.service.CenterService
import org.openurp.edu.course.site.domain.Site
import org.openurp.edu.course.site.domain.Teacher

class CenterAction(entityDao: EntityDao, centerService: CenterService) extends ActionSupport {

  @mapping("{courseId}")
  def index(@param("courseId") courseId: String): String = {
    val course = entityDao.get(classOf[Course], Numbers.toLong(courseId))
    val sites = Collections.newBuffer[Site]
    val teachers = Collections.newBuffer[Teacher]
    getCenterCourseID(course.code) match {
      case Some(centerCourseId) =>
        val url = centerService.config.courseDetailUrl(centerCourseId)
        val html = HttpUtils.getResponseText(url)
        val xml = XML.loadString(html)
        val tnodes = xml \ "DataSource" \ "TeacherList"
        tnodes.foreach { tnode =>
          teachers += new Teacher(
            (tnode \ "fUserID").head.text,
            (tnode \ "fUserName").head.text)
        }
        val cnodes = xml \ "DataSource" \ "CourseList"
        cnodes.foreach { cnode =>
          sites += new Site(
            (cnode \ "fID").head.text,
            (cnode \ "fName").head.text,
            (cnode \ "fCourseName").head.text,
            (cnode \ "fUserName").head.text,
            (cnode \ "fOrgName").head.text,
            (cnode \ "fUpdateDate").head.text,
            Numbers.toLong((cnode \ "fClicks").head.text))
        }
      case None =>
    }

    put("course", course)
    put("sites", sites.sortBy(x => 0 - x.clicks))
    put("teachers", teachers)
    put("teacherIds", teachers.map(f => (f.name, f.id)).toMap)
    put("config", centerService.config)
    forward()
  }

  private def getCenterCourseID(code: String): Option[String] = {
    val url = centerService.config.courseSearchUrl(code)
    val html = HttpUtils.getResponseText(url)
    val xml = XML.loadString(html)
    val nodes = xml \ "DataSource" \ "CourseList" \ "fID"
    if (nodes.isEmpty) {
      None
    } else {
      Some(nodes(0).text)
    }
  }
}
