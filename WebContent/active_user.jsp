<%@page import="com.zj.retrieval.master.entity.User"%>
<%@page import="java.util.List"%>
<%@page import="com.zj.retrieval.master.dao.UserDao"%>
<%@page import="com.zj.retrieval.master.Util"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="common.css" type="text/css" rel="stylesheet" />
<title>激活用户</title>
<script type="text/javascript" src="jquery-1.7.1.js"></script>
<script type="text/javascript">
	$(function() {
		$('.active_btn').click(function () {
			var a = $(this);
			a.attr('href', a.attr('href') + '&post_user_name=' + $('#post_user_name').val() + 
					'&post_user_password=' + $('#post_user_password').val());
			return true;
		});
	});
</script>
</head>
<body>
<%
	UserDao dao = Util.getUserDao();
	List<User> result = dao.getAllNotActiveUser();
%>
<div>
POST_NAME: <input id='post_user_name' type='text'/>
POST_PASSWORD: <input id='post_user_password' type='text'/>
</div>
<table style="border: solid 1px; width: 400px; margin: 10px auto;">
	<% for (User user : result) { %>
		<tr><td width="50%"><%=user.getName() %></td><td width="50%"><a class='active_btn' href='user/active?id=<%=user.getId()%>'>ACTIVE</a></td></tr>
	<% } %>
</table>
</body>
</html>