package org.openurp.edu.course.syllabus.action

import org.beangle.data.model.Entity
import org.beangle.webmvc.entity.action.RestfulAction
import java.util.Locale
import org.openurp.edu.course.syllabus.model.Revision
import org.beangle.commons.lang.SystemInfo
import org.beangle.webmvc.api.annotation.param
import org.beangle.webmvc.api.view.View
import java.io.File
import org.beangle.webmvc.api.view.Status
import org.beangle.webmvc.api.view.Stream
import org.beangle.data.dao.OqlBuilder
import org.openurp.hr.base.model.Staff
import org.openurp.platform.api.security.Securities
import org.openurp.edu.course.syllabus.service.SyllabusConfigService
import org.beangle.webmvc.api.annotation.mapping

/**
 * @author xinzhou
 */
class AbstractSyllabusAction[T <: Entity[_]] extends RestfulAction[T] {

  var syllabusConfigService: SyllabusConfigService = _

  protected def languages: Map[String, String] = {
    Map("zh" -> "中文", "en" -> "English")
  }

  override def indexSetting(): Unit = {
    put("languages", languages)
    super.indexSetting()
  }

  override def search(): String = {
    put("languages", languages)
    super.search()
  }

  def attachment(@param("revisionId") revisionId: Long): View = {
    val revision = entityDao.get(classOf[Revision], revisionId)
    if (null != revision.attachment && null != revision.attachment.path) {
      val file = new File(syllabusConfigService.syllabusBase + revision.attachment.path)
      if (file.exists) {
        Stream(file, revision.attachment.name)
      } else {
        Status(404)
      }
    } else {
      Status(404)
    }
  }

  def view(@param("revisionId") revisionId: Long): String = {
    val revision = entityDao.get(classOf[Revision], revisionId)
    if (null != revision.attachment && null != revision.attachment.path) {
      val file = new File(syllabusConfigService.syllabusBase + revision.attachment.path)
      if (file.exists) put("revision", revision)
    }
    forward()
  }

  def duplicate(entityName: String, id: Any, params: Map[String, Any]): Boolean = {
    val b = new StringBuilder("from ")
    b.append(entityName).append(" where (1=1)")
    val paramsMap = new collection.mutable.HashMap[String, Any]
    var i = 0
    for ((key, value) <- params) {
      b.append(" and ").append(key).append('=').append(":param" + i)
      paramsMap.put("param" + i, value)
      i += 1
    }
    val list = entityDao.search(b.toString(), paramsMap.toMap).asInstanceOf[Seq[Entity[_]]]
    if (!list.isEmpty) {
      if (null == id) return true
      else {
        for (e <- list) if (!(e.id == id)) return true
      }
    }
    return false
  }

  @mapping(value = "{id}")
  override def info(@param("id") id: String): String = {
    put(shortName, getModel[T](entityName, convertId(id)))
    put("languages", languages)
    forward()
  }

  def getStaff(): Staff = {
    val staffs = entityDao.search(OqlBuilder.from(classOf[Staff], "s").where("s.code=:code", Securities.user))
    if (staffs.isEmpty) {
      throw new RuntimeException("Cannot find staff with code " + Securities.user)
    } else {
      staffs.head
    }
  }

}