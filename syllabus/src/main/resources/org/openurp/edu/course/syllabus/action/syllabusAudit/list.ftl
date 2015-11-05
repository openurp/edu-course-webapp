[#ftl]
[@b.head/]
[@b.form id="revisionForm" action= "!search" name="revisionForm" ]
[@b.grid  items=revisions var="revision" sortable="false"]
  [@b.gridbar]
    bar.addItem("审核通过",action.multi('audit','确定审核通过?','&passed=1',true));
    bar.addItem("审核不通过",action.multi('audit','确定审核不通过?','&passed=0',true));
    
    function passed(passed){
      var revisionIds = bg.input.getCheckBoxValues("revision.id");
      var form = document.revisionForm;
      if(revisionIds == ""){
          alert("请选择一个教学大纲");
          return;
      }
      bg.form.addInput(form,"passed",passed);
      bg.form.submit(form, "${b.url('!save')}", 'revisionlist');
    }
    
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="syllabus.course.code" title="课程代码"]${(revision.syllabus.course.code)!}[/@]
    [@b.col width="10%" property="syllabus.course.name" title="课程名称"][@b.a href="!info?id=${revision.id!}"]${(revision.syllabus.course.name)!}[/@][/@]
    [@b.col width="10%" property="syllabus.teacher" title="上传大纲教师"]${(revision.syllabus.teacher.person.name.formatedName)!}[/@]
    [@b.col width="10%" property="syllabus.locale" title="语言"]${(revision.syllabus.locale)!}[/@]
    [@b.col width="10%" property="passed" title="审核状态"]${(revision.passed)?string('审核通过','审核不通过')!}[/@]
  [/@]
[/@]  
[/@]
[@b.foot/]