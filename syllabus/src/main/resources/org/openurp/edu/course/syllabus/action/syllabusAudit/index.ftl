[#ftl]
[@b.head/]
[@b.toolbar title='教学大纲审核' /]
<table  class="indexpanel">
  <tr>
    <td class="index_view">
    [@b.form name="revisionSearchForm"  action="!search" target="revisionlist" title="ui.searchForm" theme="search"]
      [@b.textfield name="revision.syllabus.course.code" label="课程代码"/]
      [@b.textfield name="revision.syllabus.course.name" label="课程名称"/]
      [@b.textfield name="revision.syllabus.teacher.person.name.formatedName" label="教师"/]
      [@b.select name="revision.syllabus.locale" label="语言" items=languages empty="..."/]
      <input type="hidden" name="orderBy" value="revision.id"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="revisionlist" href="!search?orderBy=revision.id" /]</td>
  </tr>
</table>
[@b.foot/]