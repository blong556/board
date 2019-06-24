<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>board</title>
</head>

<body>
<font align="center">
<p>
<b>password</b><p>
<form action="serv" method="post">
<input type="hidden" name="action" value=${param.action}>
<input type="hidden" name="id" value=${param.id}>
<input type="hidden" name="post_id" value=${param.post_id}>
<input type="password" name="password" autofocus>
<input type="submit" value="확인" size="20">
</form>
</font>
</body>
</html>