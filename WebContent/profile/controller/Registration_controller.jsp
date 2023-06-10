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
	<link href="<%= common.url %>assets/css/registration_controller.css" rel="stylesheet" />
</head>
<body>
	
<!-- 	Need to fix user session of the anchor tag -->
	<a href="<%= common.url %>profile/view/Splash.jsp">
	<img src="<%= common.url %>assets/img/logo.png" alt="logo" width="110" height="50"></a>
	
	<div class="member-signin">
		<span>Already a member? <a href="<%= common.url %>profile/controller/Sign_in_controller.jsp">Sign in</a></span>	
		
	</div>
		<h1>Create an account</h1>
		<div class="radio-buttons">
			<input type="radio" id="personal_account" name="account" value="personal_account" checked>
			<label for="personal_account">Personal account</label><br>
			<input type="radio" id="business_account" name="account" value="business_account">
			<label for="business_account">Business account</label><br>
		</div>
		
	<form>
		<div class="name-input">
			<input type="text" placeholder="First name" >
			<input type="text" placeholder="Last name" >
		</div>
	</form>

	
</body>
</html>