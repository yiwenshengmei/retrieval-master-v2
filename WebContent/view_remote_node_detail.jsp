<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="common.css" type="text/css" rel="stylesheet" />
<title>查看远程物种信息</title>
</head>
<body>
	<form action="remote_node_detail.jsp">
		<table>
			<tr><td>URL: </td><td><input name='url' type="text" style="width: 600px;"/></td></tr>
			<tr><td>NODE ID: </td><td><input name='node_id' type='text'/></td></tr>
			<tr><td>USER NAME: </td><td><input name='user_name' type="text"/></td></tr>
			<tr><td>USER PWD: </td><td><input name='user_pwd' type='text'/></td></tr>
			<tr><td colspan='2'><input value='VIEW' type='submit'/></td></tr>
			<tr><td></td><td></td></tr>
		</table>
	</form>
</body>
</html>