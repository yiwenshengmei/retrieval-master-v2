<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="common.css" type="text/css" rel="stylesheet" />
<title>上传文件</title>
</head>
<body>
<form action="file/upload" method="post" enctype="multipart/form-data" >
	PostUserName: <input type='text' name='post_user_name'/>
	PostUserPassword: <input type='text' name='post_user_password'/>
	<input name='image' type="file"/>
	<input name='submit' value='submit' type="submit"/>
</form>
</body>
</html>