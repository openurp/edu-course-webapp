package org.openurp.edu.course.syllabus.action

import org.openurp.edu.course.syllabus.model.Syllabus
import org.beangle.webmvc.api.context.Params
import org.openurp.edu.course.syllabus.model.Revision
import org.openurp.edu.base.model.Course
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.param
import javax.servlet.http.Part
import org.beangle.webmvc.api.view.View
import java.io.File
import java.io.FileOutputStream
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.Strings
import org.openurp.edu.course.syllabus.model.Attachment
import org.beangle.commons.codec.digest.Digests

/**
 * @author xinzhou
 */
class ManagerAction extends AbstractSyllabusAction[Syllabus] 