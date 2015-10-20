package org.openurp.edu.course.syllabus

import org.beangle.commons.inject.bind.AbstractBindModule
import org.openurp.edu.course.syllabus.action.StdSyllabusAction
import org.openurp.edu.course.syllabus.action.SyllabusAuditAction
import org.openurp.edu.course.syllabus.action.SyllabusGraphAction
import org.openurp.edu.course.syllabus.action.SyllabusTeacherAction
import org.openurp.edu.course.syllabus.action.SyllabusUploadAction
//remove if not needed
import scala.collection.JavaConversions._

class SyllabusModule extends AbstractBindModule {

  protected override def doBinding() {
    bind(classOf[SyllabusAuditAction], classOf[SyllabusGraphAction], classOf[SyllabusTeacherAction], 
      classOf[SyllabusUploadAction], classOf[StdSyllabusAction])
  }
}
