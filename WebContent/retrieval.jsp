<%@page import="com.zj.retrieval.master.entity.Node"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.zj.retrieval.master.RetrievalResult"%>
<%@page import="com.zj.retrieval.master.dao.RetrievalDao"%>
<%@page import="com.zj.retrieval.master.Util"%>
<%@page import="com.zj.retrieval.master.dao.NodeDao"%>

<%@page language="java" 
		contentType="text/html; charset=utf-8"
    	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="common.css" type="text/css" rel="stylesheet" />
<%
	String nodeId = request.getParameter("node_id");
	String selectedState = request.getParameter("selected_state");
	
	boolean hasResult = false;
	
	NodeDao nodeDao = Util.getNodeDao();
	Node startNode = nodeDao.getNodeById(nodeId);
	RetrievalDao retrievalDao = new RetrievalDao(startNode);
	String nodeName = startNode.getName();
	
	List<String> resultNodeIDs = null;
	String attrName = null;
	String attrNameEN = null;
	String attrDesc = null;
	List<String> attrImages = new ArrayList<String>();
	Map<String, String> attrUserFields = null;
	List<Node> rootChilds = null;
	
	if (nodeId != null && !nodeId.isEmpty()) {
		// 进入检索环节
		if (selectedState == null || selectedState.isEmpty()) 
			selectedState = "0"; // 要保证第一位必须有个数，随便是什么数字都可以，这一位以后可以用作其他用途的标志位
			RetrievalResult result = retrievalDao.retrieval(selectedState);
		
		hasResult = result.hasResult();
		if (result.hasResult()) {
			resultNodeIDs = result.getResult();
		} else {
			attrName = result.getNext().getName();
			attrNameEN = result.getNext().getEnglishName();
			attrDesc = result.getNext().getDesc();
			attrImages.add(result.getNext().getImage());
			attrUserFields = result.getNext().getUserFields();
		}
	}
%>
<title>当前节点: <%=nodeName %> 检索进度: <%=selectedState %></title>
<script type="text/javascript" src='jquery-1.7.1.js'></script>
<script type="text/javascript">
	var selected_state = '<%=selectedState%>';

	function answer(answer) {
		$('#selected_state_post').val(selected_state + answer);
		$('#retrieval_form').submit();
	}
	
	$(function() {
		
	});
</script>
</head>
<body>
	<% if (!hasResult) { %>
	<form id='retrieval_form' action="retrieval.jsp" method="post">
		<input id='selected_state_post' type="hidden" name='selected_state'/>
		<input id='node_id' type='hidden' name='node_id' value='<%=nodeId%>'/>
		<div><h1><%=attrName%></h1><h3>( <%=attrNameEN %> )</h3></div>
		<table>
			<tr><td>DESCRIPTION:&nbsp;</td><td><%=attrDesc%></td></tr>
		</table>
		
		<table>
			<tr>
			<% for (String imageURL : attrImages) { %> 
				<td><img src='<%="images/" + imageURL %>'/></td>
			<% } %>
			</tr>
		</table>
		
		<div>
			<a href='#' onclick='answer(2);'>是</a>
			<a href='#' onclick='answer(1);'>否</a>
			<a href='#' onclick='answer(3);'>不知道</a>
		</div>
	</form>
	<% } %>
	
	<% if (hasResult) { 
			List<Node> nodeResults = new ArrayList<Node>();
			for (String id : resultNodeIDs) {
				nodeResults.add(nodeDao.getNodeById(id));
			}
	%>
			<div>结果（<%=nodeResults.size() %>个）：</div>
			<table>
				<% for (Node nd : nodeResults) { %>
				<tr>
					<td><%=nd.getName() %></td>
					<td><%=nd.getEnglishName() %></td>
					<td><a href='retrieval.jsp?node_id=<%=nd.getId()%>'>继续从这里检索</a></td>
				</tr>
				<% } %>
			</table>
	<% } %>
</body>
</html>