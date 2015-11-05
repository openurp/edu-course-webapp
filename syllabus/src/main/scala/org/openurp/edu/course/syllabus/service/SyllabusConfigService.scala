package org.openurp.edu.course.syllabus.service

import org.openurp.platform.api.app.UrpApp
import org.beangle.commons.bean.Initializing
import org.beangle.commons.lang.SystemInfo

/**
 * @author xinzhou
 */
class SyllabusConfigService extends Initializing {
  var syllabusBase: String = _

  override def init(): Unit = {
    UrpApp.getUrpAppFile foreach { f =>
      scala.xml.XML.loadFile(f) \\ "syllabus" foreach { center =>
        syllabusBase = center.attribute("base").get.head.text
      }
    }
    if (null == syllabusBase) syllabusBase = SystemInfo.tmpDir

    if (!syllabusBase.endsWith("/")) syllabusBase += "/"
  }
}