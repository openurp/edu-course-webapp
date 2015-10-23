[#ftl]
[@b.head/]
[@b.grid  items=syllabuss var="syllabus" sortable="false"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="attachment.name" title="附件名称"]${(syllabus.attachment.name)!}[/@]
    [@b.col width="10%" property="locale" title="语言"]${syllabus.locale!}[/@]
    [@b.col width="10%" property="update" title="修改时间"]${syllabus.update!}[/@]
    [#--
    [@b.col width="15%" property="syllabus.attachment.name" title="教学大纲"]
      [#if syllabus.attachment??]
        [@b.a target="_blank" href="../attachment?path=${syllabus.attachment.filePath}&name=${syllabus.attachment.name?url('utf-8')}"]下载[/@]
      [/#if]
    [/@]
    --]
  [/@]
[/@]
[@b.foot/]