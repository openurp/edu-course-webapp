package org.openurp.edu.course.site.domain

/**
 * @author chaostone
 */
class CenterConfig(base: String, courseSearch: String, courseDetail: String) {

  private val courseSearchUrl = (base + "/" + courseSearch)
  private val courseDetailUrl = (base + "/" + courseDetail)
  
  def courseSearchUrl(code: String): String = {
    courseSearchUrl.replace("${code}", code)
  }

  def courseDetailUrl(courseId: String): String = {
    courseDetailUrl.replace("${courseId}", courseId)
  }
}