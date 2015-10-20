package org.openurp.edu.course.syllabus.action

import org.openurp.edu.eams.course.syllabus.helper.SyllabusGraphHelper
import org.openurp.edu.eams.system.simpleworkflow.Graph
import org.openurp.edu.eams.system.simpleworkflow.web.action.AbstractGraphAction
//remove if not needed
import scala.collection.JavaConversions._

class SyllabusGraphAction extends AbstractGraphAction {

  protected var syllabusGraphHelper: SyllabusGraphHelper = _

  protected override def getTitle(): String = "教学大纲审核流程配置"

  protected override def getGraph(): Graph = syllabusGraphHelper.getGraph

  def setSyllabusGraphHelper(syllabusGraphHelper: SyllabusGraphHelper) {
    this.syllabusGraphHelper = syllabusGraphHelper
  }
}
