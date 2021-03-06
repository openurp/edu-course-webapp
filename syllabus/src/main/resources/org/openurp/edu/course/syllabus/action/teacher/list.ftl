[#ftl]
[@b.head/]
[@b.grid  items=syllabuses var="syllabus" sortable="false"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="course.code" title="课程代码"]${(syllabus.course.code)!}[/@]
    [@b.col width="10%" property="course.name" title="课程名称"][@b.a href="!info?id=${syllabus.id!}"]${(syllabus.course.name)!}[/@][/@]
    [@b.col width="10%" property="course.department" title="开课院系"]${(syllabus.course.department.name)!}[/@]
    [@b.col width="10%" property="locale" title="语言"]${languages[syllabus.locale?string]}[/@]
    [@b.col width="10%" title="版本数"]${syllabus.revisions?size}[/@]
    [@b.col width="10%" property="update" title="最新修改时间"]${syllabus.updatedAt!}[/@]
    [@b.col width="10%" title="审核状态"]
      [#if syllabus.revisions?size>0]
        ${(syllabus.revisions?sort_by("updatedAt")?reverse?first.passed?string("审核通过","审核不通过"))!}
      [/#if]
    [/@]
  [/@]
[/@]
[@b.foot/]