<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../js/jquery-1.7.1.js"></script>
<script type="text/javascript" src="../js/jquery.ztree.core-3.5.min.js"></script>
<link rel="stylesheet" href="../css/zTreeStyle.css" type="text/css">
<title>浏览物种</title>
<SCRIPT type="text/javascript">
	var setting = {
		async: {
			enable: true,
			url:"/node/get_ztree_node",
			autoParam:["id=pid"]
		},
		data: {
			key: {
				name: "cname"
			}
		}
	};

	$(document).ready(function(){
		$.fn.zTree.init($("#treeDemo"), setting);
	});
</SCRIPT>
</head>
<body>
	<div class="zTreeDemoBackground left">
		<ul id="treeDemo" class="ztree"></ul>
	</div>
</body>
</html>