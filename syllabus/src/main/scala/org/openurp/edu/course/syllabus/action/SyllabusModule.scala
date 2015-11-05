package org.openurp.edu.course.syllabus

import org.beangle.commons.inject.bind.AbstractBindModule
import org.openurp.edu.course.syllabus.action.SyllabusTeacherAction
import org.openurp.edu.course.syllabus.action.SyllabusAuditAction
import org.openurp.edu.course.syllabus.service.SyllabusConfigService

class SyllabusModule extends AbstractBindModule {

  override def binding() {
    bind(classOf[SyllabusTeacherAction], classOf[SyllabusAuditAction])
    bind(classOf[SyllabusConfigService])
  }
}
