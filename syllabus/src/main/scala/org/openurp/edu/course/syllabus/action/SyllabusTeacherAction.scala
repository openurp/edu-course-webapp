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
    val parts = Params.getAll("attachment").asInstanceOf[List[Part]]
    for (part <- parts) {
      val attachement = new Attachment()
      attachement.size = part.getSize.toInt
      attachement.path = Digests.md5Hex(part.getName)
      attachement.name = part.getName
      IOs.copy(part.getInputStream, new FileOutputStream(SystemInfo.tmpDir + "/" + attachement.path))
      val revision = new Revision
      revision.attachment = attachement
      revision.syllabus = entity
      entity.revisions += revision
    }
    super.saveAndRedirect(entity)
  }
}