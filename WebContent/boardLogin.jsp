<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>board</title>
<style>
a {text-decoration:none; color:black;}
</style>
</head>

<body>
<p>
<form action="serv" method="post" id="frm">
<input type="hidden" name="action" value="">
<table border="1" align="center">
	<caption><a href="boardList.jsp"><b>게시판</b></a></caption>
	<tr>
		<td align="center">id</td>
		<td><input type="text" name="user_id" size="14" onkeydown="if(event.key == 'Enter') 
		{checkId(); return false;}" autofocus><input type="button" value="확인" onclick="checkId()"></td>
	</tr>
	<tr>
		<td align="center">password</td><td><input type="password" name="password"></td>
	</tr>
	<tr>
		<td colspan=2 align="center"><input type="submit" value="로그인" onclick=
			"document.getElementsByName('action')[0].value = 'login'">
		<input type="button" value="회원가입" onclick=
			"document.getElementsByName('action')[0].value = 'join'; 
			document.getElementById('frm').submit();"></td>
	</tr>
</table>
</form>
<script type="text/javascript">
function checkId() {
	var req = new XMLHttpRequest();
	req.open("post", "idCheck.jsp", true);
	req.onreadystatechange = function() {
		if(req.readyState == 4 && req.status == 200) {
			alert(req.responseText);
		}
	}
	req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	req.send("user_id=" + document.getElementsByName('user_id')[0].value);
}
</script>
</body>
</html>