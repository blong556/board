<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="board.PostEntity" %>
<%@ page import="board.BoardDB" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%!int pageVolume = 10;%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-master">
<title>board</title>
<style>
#listTable {table-layout:fixed}
#listTable td {white-space:nowrap; overflow:hidden; text-overflow:ellipsis;}
#listTable a {text-decoration:none; color:black;}
#listTable tbody a {display:inline-block; width:100%;}
</style>
</head>

<body>
<p>
<table align="center" width="600" id="listTable">
	<caption><a href="boardList.jsp"><b>게시판</b></a></caption>
	<tr bgcolor="lightskyblue" align="center">
		<td width="400"><b>글 제목</b></td>
		<td width="70"><b>작성자</b></td>
		<td width="50"><b>조회수</b></td>
		<td width="80"><b>날짜</b></td>
	</tr>
<%
BoardDB db = new BoardDB();
SimpleDateFormat simpledate = new SimpleDateFormat("yyyy.MM.dd");
ArrayList<PostEntity> listArray;
int pageCount;
int curPage = 1;
String keyword = "";

// 리스트 조건 (페이지, 검색어)
if(request.getParameter("page") != null) {
	curPage = Integer.parseInt(request.getParameter("page"));
}
if(request.getParameter("keyword") != null) {
	keyword = "&keyword=" + request.getParameter("keyword");
	pageCount = (db.getListCount(request.getParameter("keyword"))-1)/pageVolume+1;
	listArray = db.getPosts(curPage, request.getParameter("keyword"));
} else {
	pageCount = (db.getListCount("")-1)/pageVolume+1;
	listArray = db.getPosts(curPage);
}
// 리스트 출력
for(int i = 0 ; i < listArray.size(); i++) {
	PostEntity pe = listArray.get(i);
	if(i % 2 == 1) {out.println("<tr bgcolor='lightskyblue' align='center'>");}
	else {out.println("<tr bgcolor='ghostwhite' align='center'>");}
	out.println("<td><a href='boardView.jsp?id=" + pe.getId() + "'>" + pe.getTitle() + "</td>");
	if(pe.isLogon()) {out.println("<td><b>" + pe.getUserId() + "</b></td>");}
	else {out.println("<td>" + pe.getUserId() + "</td>");}
	out.println("<td>" + pe.getViews() + "</td>");
	out.println("<td>" + simpledate.format(pe.getDate()) + "</td>");
	out.println("</tr>");
}
%>
</table>
<table border="0" align="center" width="600">
	<tr>
		<td colspan="4" align="center">
<%
// 페이지번호 출력과 링크
out.println("<");
for(int i = 4; i > 0; i--){
	if(curPage-i > 0){
		out.println("<a href='boardList.jsp?page=" + (curPage-i) + keyword + "' style='color:black'>" + (curPage-i) 
			+ "</a> ");
	}
}
out.println("<b>" + curPage + "</b> ");
for(int i = 1; i < 5; i++){
	if(curPage+i <= pageCount){
		out.println("<a href='boardList.jsp?page=" + (curPage+i) + keyword + "' style='color:black'>" + (curPage+i) 
			+ "</a> ");
	}
}
out.println(">");
%>
		</td>
	</tr>
	<tr>
		<% if(session.getAttribute("login") == null) { %>
		<td><a href="boardLogin.jsp" style="color:black">로그인</a></td>
		<% } else { %>
		<td><a href="serv?action=logout" style="color:black">로그아웃 (${sessionScope.login})</a></td>
		<% } %>
		<td colspan=3 align="right"><a href="boardWrite.jsp" style="color:black">글쓰기</td>
	</tr>
	<tr>
		<td colspan=4 align="right">
			<form action="boardList.jsp" method="get">
			<input type="text" name="keyword">
			<input type="submit" value="검색">
			</form>
		</td>
	</tr>
</table>
<%db.close(); %>
</body>
</html>
