[#ftl/]
[@b.head/]
<table class="infoTable" width="100%">
  <tr>
    <td class="title" width="20%">课程代码:</td>
    <td class="content">${(revision.syllabus.course.code)!}</td>
    <td class="title" width="20%">课程名称:</td>
    <td class="content">${(revision.syllabus.course.name)!}</td>
    <td class="title" width="20%">教学大纲语言:</td>
    <td class="content">${languages[revision.syllabus.locale?string]}</td>
  </tr>
  <tr>
    <td class="title" width="20%">版本:</td>
    <td class="content" colspan="5">
        ${revision.updatedAt?string("yyyy-MM-dd HH:mm")!}
        [#if revision.attachment??]
        ${(revision.attachment.name)!}
          [@b.a target="_blank" href="!attachment?revisionId=${revision.id}"]下载[/@]
          [#if revision.attachment.path?endsWith(".pdf")]
          &nbsp;&nbsp;[@b.a target="attachment_viewer" href="!view?revisionId=${revision.id}"]预览[/@]
          [/#if]
        [/#if]
    </td>
  </tr>
</table>
<iframe id="attachment_viewer" width="100%" height="800px"/>
[@b.foot/]
