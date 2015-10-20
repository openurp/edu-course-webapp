package org.openurp.edu.course.syllabus.model

import java.util.Locale
import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.Updated
import org.beangle.data.model.TemporalOn

/**
 * 大纲的修订版
 * @author chaostone
 */
class Revision extends LongId with Updated with TemporalOn {

  var syllabus: Syllabus = _

  var attachment: Attachment = _

  var contents = Collections.newBuffer[Section]

  var passed: Boolean = _
}