<%@page import="bean.common"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div>
	
	<img src="<%= common.url %>assets/img/logo.png" alt="eBay logo" width="110" height="50">
	
		<h1>Home Page</h1>
		
		<table border="1">
			<tr>
				<td><a href="http://localhost:8080/eBay/ebay.com">Home</a></td>
				<td><a href="http://localhost:8080/eBay/profile">Profile</a></td>
				<td>Welcome!</td>
			</tr>
		</table>
	</div>

</body>
</html>