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

class CenterAction(entityDao: EntityDao, centerService: CenterService) extends ActionSupport {

  @mapping("{courseId}")
  def index(@param("courseId") courseId: String): String = {
    val course = entityDao.get(classOf[Course], Numbers.toLong(courseId))
    val sites = Collections.newBuffer[Site]
    getCenterCourseID(course.code) match {
      case Some(centerCourseId) =>
        val url = centerService.config.courseDetailUrl(centerCourseId)
        val html = HttpUtils.getResponseText(url)
        val xml = XML.loadString(html)
        val nodes = xml \ "DataSource" \ "CourseList"
        nodes.foreach { node =>
          sites += new Site(
            (node \ "fID").head.text,
            (node \ "fName").head.text,
            (node \ "fCourseName").head.text,
            (node \ "fUserName").head.text,
            (node \ "fOrgName").head.text,
            (node \ "fUpdateDate").head.text,
            Numbers.toLong((node \ "fClicks").head.text))
        }
      case None =>
    }

    put("course", course)
    put("sites", sites.sortBy(x => 0 - x.clicks))
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
