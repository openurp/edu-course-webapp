[@b.head/]
<div style="width:80%;margin:auto">
<p>课程基本信息</p>
<table class="infoTable">
	   <tr>
	     <td class="title">代码:</td>
	     <td class="content">${course.code}</td>
	     <td class="title">名称:</td>
	     <td class="content">${course.name}</td>
	   </tr>
	   <tr>
	     <td class="title">英文名:</td>
	     <td class="content">${course.enName}</td>
	     <td class="title">面向项目:</td>
         <td class="content">${course.project.name}</td>
	   </tr>
	   <tr>
	     <td class="title">学分:</td>
         <td class="content">${course.credits}</td>
	     <td class="title">周课时:</td>
         <td class="content">${course.weekHour}</td>
	   </tr>
	   <tr>
        <td class="title">学时:</td>
        <td  class="content">${course.period}</td>
        <td class="title">课程种类:</td>
        <td  class="content">${course.category.name}</td>
       </tr>
</table>
</div>
[@b.div href="center?courseId="+course.id/]
[@b.foot/]