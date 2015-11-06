package org.openurp.edu.course.syllabus.action

import java.beans.Transient
import java.io.File
import java.io.FileOutputStream

import org.beangle.commons.codec.digest.Digests
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.ignore
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.annotation.param
import org.beangle.webmvc.api.context.Params
import org.beangle.webmvc.api.view.View
import org.openurp.edu.base.model.Course
import org.openurp.edu.course.syllabus.model.Attachment
import org.openurp.edu.course.syllabus.model.Revision
import org.openurp.edu.course.syllabus.model.Syllabus

import javax.servlet.http.Part
/**
 * @author xinzhou
 */
class TeacherAction extends AbstractSyllabusAction[Syllabus] {

  override def editSetting(entity: Syllabus): Unit = {
    put("languages", languages)
    val query = OqlBuilder.from(classOf[Course], "course").join("course.teachers", "t").where("t.id=:staffId", getStaff.id)
    val courses = entityDao.search(query)
    put("courses", courses)
    super.editSetting(entity)
  }

  protected override def getQueryBuilder(): OqlBuilder[Syllabus] = {
    val builder = super.getQueryBuilder()
    builder.where("syllabus.teacher=:teacher", getStaff)
  }

  override def saveAndRedirect(entity: Syllabus): View = {
    val teacher = getStaff
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
      val ext = Strings.substringAfterLast(part.getSubmittedFileName, ".")
      attachment.path = Digests.md5Hex(part.getSubmittedFileName) + (if (Strings.isEmpty(ext)) "" else "." + ext)
      attachment.name = part.getSubmittedFileName
      IOs.copy(part.getInputStream, new FileOutputStream(syllabusConfigService.syllabusBase + attachment.path))
      val revision = new Revision
      revision.attachment = attachment
      revision.syllabus = entity
      revision.updatedAt = new java.util.Date
      entity.revisions += revision
    }
    super.saveAndRedirect(entity)
  }

  def removeRevision(@param("revisionId") revisionId: Long): View = {
    val revision = entityDao.get(classOf[Revision], revisionId)
    try {
      if (revision.passed) {
        redirect("search", "大纲已审核通过,删除失败")
      } else {
        remove(revision)
        val file = new File(syllabusConfigService.syllabusBase + revision.attachment.path)
        if (file.exists()) file.delete()
        redirect("search", "info.remove.success")
      }
    } catch {
      case e: Exception => {
        logger.info("removeAndForwad failure", e)
        redirect("search", "info.delete.failure")
      }
    }
  }

  override def remove(): View = {
    val syllabuses = entityDao.find(classOf[Syllabus], longIds("syllabus"))
    for (syllabus <- syllabuses) {
      syllabus.revisions.foreach { revision =>
        val file = new File(syllabusConfigService.syllabusBase + revision.attachment.path)
        if (file.exists()) file.delete()
      }
    }
    super.remove()
  }
}