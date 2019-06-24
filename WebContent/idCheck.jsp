<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="board.BoardDB" %>
<%
	BoardDB db = new BoardDB();
	String message;
	
	if(db.checkId(request.getParameter("user_id"))) {
		message = "중복된 아이디입니다.";
	} else {
		message = "사용할 수 있는 아이디입니다.";
	}
	db.close();
%>
<%=message %>