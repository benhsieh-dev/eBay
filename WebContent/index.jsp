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
	<a href="<%= common.url %>splash">
		<img src="<%= common.url %>assets/img/logo.png" alt="eBay logo" width="110" height="50">
	</a>
	<div class="form" style="width:1000px;margin:auto;">
		<h1>Hello</h1>
		<p>Sign in to eBay or <a href="<%= common.url %>profile/controller/Registration_controller.jsp">create an account</a></p>
<%-- 	<p>Sign in to eBay or <a href="<%= common.url %>WEB-INF/view/Registration_controller.jsp">create an account</a></p>	 --%>
		<form action="profile/controller/Sign_in_controller.jsp" method="post">					
			<input type="text" name="user_name" placeholder="Email or username"> <br>
			<input type="password" name="password" placeholder="Password"><br>		
			<input type="submit" value="Continue" id="submit">
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