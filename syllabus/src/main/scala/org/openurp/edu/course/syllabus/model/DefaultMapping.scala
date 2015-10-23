package org.openurp.edu.course.syllabus.model

import org.beangle.data.model.bind.Mapping

class DefaultMapping extends Mapping {

  def binding(): Unit = {
    defaultIdGenerator("auto_increment")

    bind[Syllabus].on(e => declare(
      e.course & e.teacher & e.locale are notnull,
      e.revisions is depends("syllabus")))

    bind[Revision].on(e => declare(
      e.syllabus & e.attachment & e.passed are notnull,
      e.contents is depends("Section"),
      e.attachment.name is (notnull, length(50)),
      e.attachment.size is (notnull, column("file_size")),
      e.attachment.path is (notnull, length(200))))

    bind[Section].on(e => declare(
      e.title & e.revision are notnull,
      e.content is length(200)))

    bind[SessionTitle].on(e => declare(
      e.name is (notnull, length(50))))
  }
}