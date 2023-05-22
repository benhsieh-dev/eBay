<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div style="width:1000px;margin:auto;">
		<h3>Login Here</h3>
		<form action="profile/controller/Sign_in_controller.jsp" method="post">
			Enter User Name
			<input type="text" name="user_name"> <br>
				Enter Password
			<input type="password" name="password"><br>
			<input type="submit" value="Submit">
		</form>
		
		<%
			String message = ""; 
		%>
	</div>
</body>
</html>