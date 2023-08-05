<%@page import="bean.common"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Electronics, Cars, Fashion, Collectibles & More | eBay</title>
	<link rel="icon" type="image/png" href="<%= common.url %>assets/img/favicon.png" />
</head>
<body>
	<div>
	
	<h3>${welcomeMessage}</h3>
	
	<p>Hi! <a href="<%= common.url %>index.jsp">Signin</a> or <a href="<%= common.url %>profile/controller/Registration_controller.jsp">register</a></p>
	
	<img src="<%= common.url %>assets/img/logo.png" alt="eBay logo" width="110" height="50">
	
<!-- 		<h1>Home Page</h1>
		
		<table border="1">
			<tr>
				<td><a href="http://localhost:8080/eBay/ebay.com">Home</a></td>
				<td><a href="http://localhost:8080/eBay/profile">Profile</a></td>
				<td>Welcome!</td>
			</tr>
		</table> -->
	</div>

</body>
</html>