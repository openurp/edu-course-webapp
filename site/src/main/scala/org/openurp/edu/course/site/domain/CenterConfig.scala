package org.openurp.edu.course.site.domain

/**
 * @author chaostone
 */
class CenterConfig(base: String, courseSearch: String, courseDetail: String, courseSite: String,teacherDetail:String) {

  private val courseSearchUrl = (base + "/" + courseSearch)
  private val courseDetailUrl = (base + "/" + courseDetail)
  private val courseSiteUrl = (base + "/" + courseSite)
  private val teacherDetailUrl = (base + "/" + teacherDetail)

  def courseSearchUrl(code: String): String = {
    courseSearchUrl.replace("${code}", code)
  }

  def courseDetailUrl(courseId: String): String = {
    courseDetailUrl.replace("${courseId}", courseId)
  }

  def courseSiteUrl(siteId: String): String = {
    courseSiteUrl.replace("${siteId}", siteId)
  }
  def teacherDetailUrl(teacherId: String): String = {
    teacherDetailUrl.replace("${teacherId}", teacherId)
  }
}