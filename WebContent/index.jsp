<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="bean.common"  %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Sign in or Register | eBay</title>
	<link href="<%= common.url %>/assets/css/index.css" rel="stylesheet" />
</head>
<body>
	<div style="width:1000px;margin:auto;">
		<h1>Hello</h1>
		<p>Sign in to eBay or create an account</p>
		<form action="profile/controller/Sign_in_controller.jsp" method="post">
			Enter User Name
			<input type="text" name="user_name"> <br>
				Enter Password
			<input type="password" name="password"><br>
			<input type="submit" value="Submit">
		</form>
		
		<%
			String message = (String) session.getAttribute("login message"); 
		
			if (message != null) {
				out.println(message); 
				session.removeAttribute("login message");
			}
		%>
	</div>
</body>
</html>