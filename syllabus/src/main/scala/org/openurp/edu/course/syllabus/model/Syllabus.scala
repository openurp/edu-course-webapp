package org.openurp.edu.course.syllabus.model

import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.TemporalOn
import org.beangle.data.model.Updated
import org.openurp.edu.base.model.Course
import org.openurp.hr.base.model.Staff
import java.util.Locale

/**
 * @author chaostone
 */
class Syllabus extends LongId with Updated {

  var course: Course = _

  var teacher: Staff = _

  var revisions = Collections.newBuffer[Revision]

  var locale: Locale = _
}