<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="board.BoardDB" %>
<%@ page import="board.PostEntity" %>
<%@ page import="board.CommentEntity" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>board</title>
</head>

<body>
<%
request.setCharacterEncoding("UTF-8");
int postId = Integer.parseInt(request.getParameter("id"));
BoardDB db = new BoardDB();
PostEntity be = db.getPost(postId);
ArrayList<CommentEntity> commentList = db.getComments(postId);
String fileName = db.getPost(postId).getFileName();
String fileURL = db.getPost(postId).getFileURL();
SimpleDateFormat simpleDate = new SimpleDateFormat("yy/MM/dd HH:mm");

// 조회수 증가(쿠키)
boolean countView = true;
String viewedId = "";
for(Cookie cookie : request.getCookies()){
	if(cookie.getName().equals("view")) {
		viewedId = cookie.getValue() + ":";
		for(String viewCookie : cookie.getValue().split(":")) {
			if(viewCookie.equals(request.getParameter("id"))) {countView = false;}
		}
	}
}
if(countView) {
	Cookie cookie = new Cookie("view", viewedId + postId);
	response.addCookie(cookie);
	db.viewPlus(postId);
}
%>
<br><br>
<!-- 게시글 출력 -->
<table width="600" align="center" style="border-collapse:collapse">
	<tr bgcolor="lightskyblue" style="border:solid 2px white">
		<td colspan="3"><%=be.getTitle() %></td>
	</tr>
	<tr height="280" bgcolor="ghostwhite" valign="top">
		<td colspan="3">
			<%
			if(db.isImage(be.getFileURL())) {out.println("<img src='uploadStorage/" + be.getFileURL()
				+ "' style='max-width:100%'><br>");}
			%>
			<%=be.getContent() %>
		</td>
	</tr>
	<tr bgcolor="ghostwhite">
		<%if(db.getPost(postId).isLogon()) {%>
		<td colspan="3" align="right"><%=simpleDate.format(be.getDate())%>&emsp;작성자:<b><%=be.getUserId() %></b>&nbsp;</td>
		<%} else { %>
		<td colspan="3" align="right"><%=simpleDate.format(be.getDate())%>&emsp;작성자:<%=be.getUserId() %>&nbsp;</td>
		<%} %>
	</tr>
	<tr>
		<td colspan="2">
			<%if(fileName != null) { %>
			첨부파일 : <a href="uploadStorage/<%=fileURL %>"><font color="black"><%=fileName %></font></a>
			<%} %>
		</td>
		<td align="right">
			<span onclick="editPost()" style="cursor:pointer"><u>수정</u></span></a>
			<span onclick="deletePost()" style="cursor:pointer"><u>삭제</u></span>
			<span onclick="window.location.href='boardList.jsp'" style="cursor:pointer"><u>목록</u></span></form></td>
		</td>
	</tr>
</table>
<!-- 코멘트 출력 -->
<table width="600" align="center">
	<tr>
		<td colspan="3"><b>댓글</b></td>
	</tr>
	<%
	for(CommentEntity ce : commentList) {
		int depth = ce.getDepth();
		String replyMark = "";
		if(depth != 0) {replyMark = "re: ";}
		out.println("<tr>");
		if(ce.isLogon()) {out.println("<td width=80><b><span>" + ce.getUserId() + "</span></b></td>");} 
		else {out.println("<td width=80><span>" + ce.getUserId() + "</span></td>");}
		out.println("<td style='padding-left:" + ce.getDepth()*10 + "px'>" + replyMark);
		out.println("<span>" + ce.getContent() + "</span></td>");
		out.println("<td align='right' width='120' data-id=" + ce.getId() + ">");
		out.println("<font size='1' style='cursor:pointer' onclick='replyComment(event)'>답글</font>");
		out.println("<font size='1' style='cursor:pointer' onclick='modifyComment(event)'>수정</font>");
		out.println("<font size='1' style='cursor:pointer' onclick='deleteComment(event)'>삭제</font>");
		out.println("<font size='1'> " + simpleDate.format(ce.getDate()).replace(" ","<br>") + "</font></td>");
		out.println("</tr>");
	}
	%>
</table>
<!-- 코멘트 작성 -->
<form action="" method="post" id="writeForm" onsubmit="submitFormAJAX(); return false;">
<input type="hidden" name="action" value="insertComment" id="action">
<input type="hidden" name="post_id" value=${param.id}>
<input type="hidden" name="target_id" value="0">
<input type="hidden" name="depth" value="0">
<input type="hidden" name="logon" value="0">
<table width=600 align="center" id="writeTable">
	<tr>
		<td width="70">
			<%if(session.getAttribute("login") == null) { %>
			id<br><input type="text" name="user_id" size="5" onkeydown="return blockSubmit(event)"><br>password<br>
			<input type="password" name="password" size="5" onkeydown="return blockSubmit(event)">
			<%} else { %>
			<br>id<br><b>${sessionScope.login}</b><br><br>
			<input type="hidden" name="user_id" value=${sessionScope.login}>
			<input type="hidden" name="password" value="null">
			<%} %>
		</td>
		<td width="470"><br><textarea name="content" rows="4" style="width:99%"></textarea></td>
		<td><br><br><br><input type="submit" value="작성"></td>
	</tr>
</table>
</form>

<script src="https://code.jquery.com/jquery-3.3.1.js"></script>
<script type="text/javascript">
function editPost() {
	var frm = document.createElement("form");
	frm.action = "serv";
	frm.method = "post";
	frm.appendChild(createInput("id", "${param.id}"));
	frm.appendChild(createInput("action", "editPost"));
	document.body.appendChild(frm);
	frm.submit();
}

function deletePost() {
	var frm = document.createElement("form");
	frm.action = "serv";
	frm.method = "post";
	frm.appendChild(createInput("id", "${param.id}"));
	frm.appendChild(createInput("action", "deletePost"));
	if(<%=db.getPost(postId).isLogon() %> && "${sessionScope.login}" == "<%=db.getPost(postId).getUserId() %>") {
		if(confirm("삭제하시겠습니까?")) {
			document.body.appendChild(frm);
			frm.submit();
		}
	} else {
		document.body.appendChild(frm);
		frm.submit();
	}
}

function submitFormAJAX() {
	var req = new XMLHttpRequest();
	var params = $("#writeForm").serialize();
	req.open("post", "serv", true)
	req.onreadystatechange = function() {
		if(req.readyState == 4) {
			if(req.status == 200) {
				eval(req.responseText);
			} else {
				alert("status " + req.status + ":에러가 발생했습니다.");
			}
		}
	}
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.send(params);
}

function replyComment(event) {
	var innerValue = "<br><input type='button' value='답글' onclick='undoReplyComment()'><br><br><input type='submit' " +
		"value='작성'>";
	document.getElementById("action").value = "insertComment";
	document.getElementsByName("target_id")[0].value = event.target.parentElement.getAttribute("data-id");
	document.getElementsByName("depth")[0].value = event.target.parentElement.parentElement.cells[1].style.paddingLeft.replace(/[^0-9]/g,"")/10+1;
	document.getElementById("writeTable").rows[0].cells[2].innerHTML = innerValue;
	document.getElementsByName("content")[0].focus();
}

function undoReplyComment() {
	var innerValue = "<br><br><br><input type='submit' value='작성'>"
	document.getElementsByName("target_id")[0].value = 0;
	document.getElementsByName("depth")[0].value = 0;
	document.getElementById("writeTable").rows[0].cells[2].innerHTML = innerValue;
}

function modifyComment(event) {
	var userId = event.target.parentElement.parentElement.cells[0].getElementsByTagName("span")[0].innerHTML;
	var content = event.target.parentElement.parentElement.cells[1].getElementsByTagName("span")[0].innerHTML;
	var innerValue = "<br><input type='button' value='수정' onclick='undoModifyComment()'><br><br><input type='submit'" +
	" value='작성'>";
	document.getElementById("action").value = "updateComment";
	document.getElementById("writeTable").rows[0].cells[0].getElementsByTagName("input")[0].value = userId;
	document.getElementById("writeTable").rows[0].cells[1].getElementsByTagName("textarea")[0].innerHTML = content;
	document.getElementById("writeTable").rows[0].cells[2].innerHTML = innerValue;
	document.getElementsByName("target_id")[0].value = event.target.parentElement.getAttribute("data-id");
}

function undoModifyComment() {
	var innerValue = "<br><br><br><input type='submit' value='작성'>"
	document.getElementById("action").value = "insertComment";
	document.getElementById("writeTable").rows[0].cells[0].getElementsByTagName("input")[0].value = "";
	document.getElementById("writeTable").rows[0].cells[1].getElementsByTagName("textarea")[0].innerHTML = "";
	document.getElementById("writeTable").rows[0].cells[2].innerHTML = innerValue;
	document.getElementsByName("target_id")[0].value = 0;
}

function deleteComment(event) {
	var frm = document.createElement("form");
	frm.action = "serv";
	frm.method = "post";
	frm.appendChild(createInput("id", event.target.parentElement.getAttribute("data-id")));
	frm.appendChild(createInput("post_id", "${param.id}"));
	frm.appendChild(createInput("action", "deleteComment"));
	if(event.target.parentElement.parentElement.cells[0].getElementsByTagName("b")[0] != null && "${sessionScope.login}" == event.target.parentElement.parentElement.cells[0].getElementsByTagName("span")[0].innerHTML) {
		if(confirm("삭제하시겠습니까?")) {
			document.body.appendChild(frm);
			frm.submit();
		}
	} else {
		document.body.appendChild(frm);
		frm.submit();
	}
}

function createInput(name, value) {
	var input = document.createElement("input");
	input.type = "hidden";
	input.name = name;
	input.value = value;
	return input;
}
function blockSubmit(event) {
	if(event.key == "Enter") {
		return false;
	}
}
</script>
<%db.close(); %>
</body>
</html>