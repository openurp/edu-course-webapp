package org.openurp.edu.course.site.web

import org.beangle.commons.inject.bind.AbstractBindModule
import org.openurp.edu.course.site.web.action.CenterAction
import org.openurp.edu.course.site.web.action.IndexAction

class DefaultModule extends AbstractBindModule {

  override def binding() {
    bind(classOf[CenterAction],classOf[IndexAction])
  }

}