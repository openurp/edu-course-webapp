package org.openurp.edu.course.syllabus.model

import org.beangle.data.model.Component
import org.beangle.data.model.Named

/**
 * @author chaostone
 */
class Attachment extends Component with Named {
  var size: Int = _
  var path: String = _
}