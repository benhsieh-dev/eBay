<%@page import="bean.common"%>
<%@page import="modal.Login_Modal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Register: Create a personal account</title>
	<link rel="icon" type="image/png" href="<%= common.url %>assets/img/favicon.png" />
</head>
<body>
	
<!-- 	Need to fix user session of the anchor tag -->
	<a href="<%= common.url %>profile/view/Splash.jsp">
	<img src="<%= common.url %>assets/img/logo.png" alt="logo" width="110" height="50">
	</a>
	
	<form>
		<h2>Create an account</h2>
		<input type="radio" id="personal_account" name="personal_account" value="personal_account">
		<label for="personal_account">Personal account</label><br>
		<input type="radio" id="business_account" name="business_account" value="business_account">
		<label for="business_account">Business account</label><br>
	</form>

	
</body>
</html>