package org.openurp.edu.course.syllabus

import org.beangle.commons.inject.bind.AbstractBindModule
import org.openurp.edu.course.syllabus.action.SyllabusTeacherAction

class SyllabusModule extends AbstractBindModule {

  override def binding() {
    bind(classOf[SyllabusTeacherAction])
  }
}
