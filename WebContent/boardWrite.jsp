<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "board.BoardDB" %>
<%@ page import = "board.PostEntity" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>board</title>
</head>

<body>
<%
BoardDB db = new BoardDB();
PostEntity entity;
%>
<p>
<form action="serv" method="post" onsubmit="false" enctype="multipart/form-data">
<table align="center">
	<tr><td>
	<%
	// 글 수정
	if(request.getParameter("action") != null && request.getParameter("action").equals("editPost")) {
		db = new BoardDB();
		entity = db.getPost(Integer.parseInt(request.getParameter("id")));
	%>
	<input type="hidden" name="action" value="updatePost">
	<input type="hidden" name="id" value=${param.id}>
	<input type="hidden" name="password" value=${param.password}>
	<input type="hidden" name="user_id" value="<%=entity.getUserId() %>">
	<input type="hidden" name="logon" value="<%=entity.isLogon() %>">
	이름 : <%=entity.getUserId() %><p>
	제목 : <input type="text" name="title" size="69" value="<%=entity.getTitle() %>" onkeydown="return blockEnter(event);"><p>
	내용 : <textarea name="content" cols="70" rows="15"><%=entity.getContent() %></textarea>
	<input type="submit" value="글쓰기">
	<%
	// 글 작성
	} else {
	%>
	<input type="hidden" name="action" value="insertPost">
	<% if(session.getAttribute("login") == null) { %>
	<input type="hidden" name="member_id" value="${sessionScope.login}">
	이름 : <input type="text" name="user_id" size=20 onkeydown="return blockEnter(event);">&emsp;
	비밀번호 : <input type="password" name="password" size="20" onkeydown="return blockEnter(event);"><p>
	<% } else { %>
	<input type="hidden" name="user_id" value=${sessionScope.login}> 
	<input type="hidden" name="password" value="null">
	<input type="hidden" name="logon" value="true">
	이름 : ${sessionScope.login}<p>
	<% } %>
	제목 : <input type="text" name="title" size="69" onkeydown="return blockEnter(event);"><p>
	내용 : <textarea name="content" cols=70 rows="15"></textarea><p>
	첨부 : <input type="file" name="file">
	<input type="submit" value="글쓰기">
	<% } %>
	</td></tr>
</table>
</form>

<script type="text/javascript">
function blockEnter(event) {
	if(event.key == 'Enter') {
		return false;
	}
}
</script>
<%db.close(); %>
</body>
</html>