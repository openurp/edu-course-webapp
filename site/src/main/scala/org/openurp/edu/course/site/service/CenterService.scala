package org.openurp.edu.course.site.service

import org.openurp.edu.course.site.domain.CenterConfig

/**
 * @author chaostone
 */
trait CenterService {
  def config: CenterConfig
}