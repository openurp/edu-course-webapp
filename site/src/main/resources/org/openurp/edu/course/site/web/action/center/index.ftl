[@b.head/]
<div style="width:80%;margin:auto">
<p>课程中心 站点资源</p>
[@b.grid items=sites var="site" sortable="false"]
  [@b.row]
    [@b.col title="序号" width="10%"]${site_index+1}[/@]
    [@b.col property="courseName" title="课程" width="35%"]
    <li><a href="http://cec.shfc.edu.cn/G2S/Template/View.aspx?action=view&courseType=0&courseId=${site.id}">${site.courseName}</a></li>
    [/@]
    [@b.col property="userName" title="负责人" width="10%"/]
    [@b.col property="orgName" title="院系" width="20%"/]
    [@b.col property="updatedOn" title="更新日期" width="15%"/]
    [@b.col property="clicks" title="访问量" width="10%"/]
  [/@]
[/@]
</div>
[@b.foot/]