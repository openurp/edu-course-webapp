package org.openurp.edu.course.syllabus

import org.beangle.commons.inject.bind.AbstractBindModule
import org.openurp.edu.course.syllabus.service.SyllabusConfigService
import org.openurp.edu.course.syllabus.action.AuditAction
import org.openurp.edu.course.syllabus.action.ManagerAction
import org.openurp.edu.course.syllabus.action.TeacherAction

class SyllabusModule extends AbstractBindModule {

  override def binding() {
    bind(classOf[TeacherAction], classOf[AuditAction],classOf[ManagerAction])
    bind(classOf[SyllabusConfigService])
  }
}
