package org.openurp.edu.course.syllabus.model

import org.beangle.data.model.IntId
import org.beangle.data.model.Named
import org.beangle.data.model.LongId

/**
 * @author chaostone
 */
class Section extends LongId {
  var title: SessionTitle = _
  var revision: Revision = _
  var content: String = _
}