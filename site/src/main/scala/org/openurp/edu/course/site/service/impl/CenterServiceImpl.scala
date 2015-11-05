package org.openurp.edu.course.site.service.impl

import org.beangle.commons.bean.Initializing
import org.openurp.edu.course.site.domain.CenterConfig
import org.openurp.edu.course.site.service.CenterService
import org.openurp.platform.api.app.UrpApp

/**
 * @author chaostone
 */
class CenterServiceImpl extends CenterService with Initializing {

  var _config: CenterConfig = _

  def config: CenterConfig = {
    _config
  }
  override def init() {
    UrpApp.getUrpAppFile foreach { f =>
      scala.xml.XML.loadFile(f) \\ "center" foreach { center =>
        val base = center.attribute("base").get.head.text
        var courseSearch: String = null
        var courseDetail: String = null
        var courseSite: String = null
        var teacherDetail: String = null
        (center \\ "courseSearch") foreach { cs =>
          courseSearch = cs.child.head.text
        }
        (center \\ "courseDetail") foreach { cs =>
          courseDetail = cs.child.head.text
        }
        (center \\ "courseSite") foreach { cs =>
          courseSite = cs.child.head.text
        }
        (center \\ "teacherDetail") foreach { cs =>
          teacherDetail = cs.child.head.text
        }
        _config = new CenterConfig(base, courseSearch, courseDetail, courseSite, teacherDetail)
      }
    }
  }
}