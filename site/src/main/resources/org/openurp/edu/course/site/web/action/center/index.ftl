[@b.head/]
<div style="width:80%;margin:auto">
<p>课程中心 站点资源</p>

[#list teachers as teacher]
   <a href="${config.teacherDetailUrl(teacher.id)}">${teacher.name}</a>&nbsp;
[/#list]

[@b.grid items=sites var="site" sortable="false"]
  [@b.row]
    [@b.col title="序号" width="10%"]${site_index+1}[/@]
    [@b.col property="courseName" title="课程" width="35%"]<a href="${config.courseSiteUrl(site.id)}">${site.courseName}</a>[/@]
    [@b.col property="userName" title="负责人" width="10%"]
    [#if teacherIds[site.userName]??]
       <a href="${config.teacherDetailUrl(teacherIds[site.userName])}">${site.userName}</a>
    [#else]
       ${site.userName}
    [/#if]
    [/@]
    [@b.col property="orgName" title="院系" width="20%"/]
    [@b.col property="updatedOn" title="更新日期" width="15%"/]
    [@b.col property="clicks" title="访问量" width="10%"/]
  [/@]
[/@]
</div>
[@b.foot/]