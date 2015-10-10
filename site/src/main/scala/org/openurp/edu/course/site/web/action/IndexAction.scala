package org.openurp.edu.course.site.web.action

import org.beangle.commons.lang.Numbers
import org.beangle.data.dao.EntityDao
import org.beangle.webmvc.api.action.ActionSupport
import org.beangle.webmvc.api.annotation.action
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.annotation.param
import org.openurp.edu.base.model.Course

@action("")
class IndexAction(entityDao: EntityDao) extends ActionSupport {

  @mapping("{courseId}")
  def index(@param("courseId") courseId: String): String = {
    val course = entityDao.get(classOf[Course], Numbers.toLong(courseId))
    put("course", course)
    forward()
  }
}