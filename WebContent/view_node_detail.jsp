<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.zj.retrieval.master.springmvc.controller.XMLUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.zj.retrieval.master.entity.Attribute"%>
<%@page import="java.util.List"%>
<%@page import="com.zj.retrieval.master.DetailType"%>
<%@page import="com.zj.retrieval.master.entity.Node"%>
<%@page import="com.zj.retrieval.master.Util"%>
<%@page import="com.zj.retrieval.master.dao.NodeDao"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="common.css" type="text/css" rel="stylesheet" />
<title>查看物种详细信息</title>
</head>
<body>
<table width="95%" style='margin: 0 auto;'>
<%
	String name = "";
	String name_en = "";
	String parent_id = "";
	String desc = "";
	String owl = "";
	String label = "";
	String contact = "";
	String uri = "";
	String uri_name = "";
	List<String> images = new ArrayList<String>();
	List<String> child_nodes = new ArrayList<String>();
	List<Attribute> attrs = new ArrayList<Attribute>();
	String node_id = request.getParameter("node_id");
	Map<String, String> user_filed = new HashMap<String, String>();
	
	try {	
		NodeDao ns = Util.getNodeDao();
		
		Node nd = ns.getNodeById(node_id);
		name = nd.getName();
		name_en = nd.getEnglishName();
		parent_id = nd.getParentId();
		
		boolean isFull = (nd.getDetailType() == DetailType.FULL);
		if (isFull) {
			images = nd.getImages();
			uri = nd.getUri();
			uri_name = nd.getUriName();
			desc = nd.getDesc();
			owl = Util.html(XMLUtil.format(nd.getOwl(), 4));
			label = nd.getLabel();
			child_nodes = nd.getRetrievalDataSource().getChildNodes();
			attrs = nd.getRetrievalDataSource().getAttributes();
			user_filed = nd.getUserfields();
		} else {
			contact = nd.getContact();
		}
		
		
	} catch (Exception ex) {
		out.print(ex.getMessage());
	}
%>
<tr><td>ID: </td><td><%=node_id%></td></tr>
<tr><td>NAME: </td><td><%=name%></td></tr>
<tr><td>NAME_EN: </td><td><%=name_en%></td></tr>
<tr><td>DESC:</td><td><%=desc%></td></tr>
<tr><td>OWL: </td><td><%=owl%></td></tr>
<tr><td>URI: </td><td><%=uri%></td></tr>
<tr><td>URI_NAME: </td><td><%=uri_name%></td></tr>
<tr><td>PARENT_ID: </td><td><%=parent_id%></td></tr>
<tr><td>LABEL: </td><td><%=label%></td></tr>
<tr><td>CONTACT: </td><td><%=contact%></td></tr>
<tr><td colspan='2'>====== User Field ======</td></tr>
<%
	for (Entry<String, String> entry : user_filed.entrySet()) {
%>
<tr><td>KEY: <%=entry.getKey()%></td><td>VALUE: <%=entry.getValue()%></td></tr>
<%	}  %>
<tr><td colspan='2'>====== Images ======</td></tr>
<%
	for (String image_url : images) {
%>
<tr><td colspan='2'><img src='<%=image_url%>'/></td></tr>
<%
	}
%>
<tr><td colspan='2'>====== Attributes ======</td></tr>
<% for (Attribute attr : attrs) { %>
<tr><td>attr_name</td><td><%=attr.getName() %></td></tr>
<tr><td>attr_name_en</td><td><%=attr.getEnglishName() %></td></tr>
<tr><td>attr_desc</td><td><%=attr.getDesc() %></td></tr>
<tr><td>attr_image</td><td><img src='<%="images/" + attr.getImage() %>'/></td></tr>
<tr><td colspan='2'>====== ====== ======</td></tr>
<% for (Entry<String, String> entry : attr.getUserFields().entrySet()) { %>
<tr><td>KEY: <%=entry.getKey() %></td><td>VALUE: <%=entry.getValue() %></td></tr>
<% } %>
<% } %>
<tr><td></td><td></td></tr>
<tr><td></td><td></td></tr>
<tr><td></td><td></td></tr>
<tr><td></td><td></td></tr>
</table>

</body>
</html>