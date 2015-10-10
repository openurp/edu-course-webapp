package org.openurp.edu.course.site.web.action

import org.beangle.webmvc.api.action.ActionSupport
import org.beangle.data.model.dao.EntityDao
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.annotation.param
import org.openurp.edu.base.model.Course
import org.beangle.commons.lang.Numbers
import scala.xml.XML
import org.beangle.commons.web.util.HttpUtils
import org.beangle.commons.collection.Collections
import org.beangle.webmvc.api.annotation.action

@action("")
class IndexAction(entityDao: EntityDao) extends ActionSupport {

  @mapping("{courseId}")
  def index(@param("courseId") courseId: String): String = {
    val course = entityDao.get(classOf[Course], Numbers.toLong(courseId))
    put("course", course)
    forward()
  }
}