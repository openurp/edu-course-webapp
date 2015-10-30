package org.openurp.edu.course.syllabus.action

import java.io.FileInputStream
import java.util.Locale
import org.beangle.commons.collection.page.PageLimit
import org.beangle.commons.io.IOs
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.context.Params
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.Course
import org.openurp.edu.course.syllabus.model.Attachment
import org.openurp.edu.course.syllabus.model.Syllabus
import javax.servlet.http.Part
import org.beangle.commons.lang.SystemInfo
import java.io.FileOutputStream
import org.openurp.edu.course.syllabus.model.Revision
import org.beangle.commons.codec.digest.Digests
import org.openurp.hr.base.model.Staff
import org.beangle.webmvc.api.annotation.param
import org.beangle.webmvc.api.view.{ Status, Stream }
import java.io.File
import org.beangle.webmvc.api.context.ActionContext
import org.beangle.webmvc.api.context.ActionContextHolder
import org.beangle.commons.web.url.UrlBuilder
import org.beangle.data.model.Entity
/**
 * @author xinzhou
 */
class SyllabusTeacherAction extends RestfulAction[Syllabus] {

  override def indexSetting(): Unit = {
    val locales = List(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
    put("localeList", locales)
    super.indexSetting()
  }

  override def editSetting(entity: Syllabus): Unit = {
    val locales = List(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
    put("localeList", locales)
    val builder = OqlBuilder.from(classOf[Course]).limit(PageLimit(1, 20))
    val courses = entityDao.search(builder)
    put("courses", courses)
    super.editSetting(entity)
  }

  override def saveAndRedirect(entity: Syllabus): View = {
    val teacher = entityDao.get(classOf[Staff], 13538L)
    if (!entity.persisted) {
      if (duplicate(classOf[Syllabus].getName, null, Map("teacher" -> teacher, "course" -> entity.course, "locale" -> entity.locale))) {
        return redirect("search", "该课程大纲存在,请修改大纲")
      }
    }
    entity.teacher = teacher
    val parts = Params.getAll("attachment").asInstanceOf[List[Part]]
    for (part <- parts) {
      val attachment = new Attachment()
      attachment.size = part.getSize.toInt
      attachment.path = Digests.md5Hex(part.getName)
      attachment.name = part.getName
      IOs.copy(part.getInputStream, new FileOutputStream(SystemInfo.tmpDir + "/" + attachment.path))
      val revision = new Revision
      revision.attachment = attachment
      revision.syllabus = entity
      revision.updatedAt = new java.util.Date
      entity.revisions += revision
    }
    super.saveAndRedirect(entity)
  }

  def attachment(@param("revisionId") revisionId: Long): View = {
    val revision = entityDao.get(classOf[Revision], revisionId)
    if (null != revision.attachment && null != revision.attachment.path) {
      val file = new File(SystemInfo.tmpDir + "/" + revision.attachment.path)
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
      val file = new File(SystemInfo.tmpDir + "/" + revision.attachment.path)
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
}