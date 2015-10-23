[#ftl]
[@b.head/]
[@b.toolbar title='教学大纲维护' /]
<table  class="indexpanel">
  <tr>
    <td class="index_view">
    [@b.form name="syllabusSearchForm"  action="!search" target="syllabuslist" title="ui.searchForm" theme="search"]
      [@b.select name="syllabus.locale" label="语言" items=localeList option="language,language" empty="..."/]
      <input type="hidden" name="orderBy" value="syllabus.id"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="syllabuslist" href="!search?orderBy=syllabus.id" /]</td>
  </tr>
</table>
[@b.foot/]