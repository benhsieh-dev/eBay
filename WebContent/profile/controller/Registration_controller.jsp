<%@page import="bean.common"%>
<%@page import="modal.Login_Modal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Register: Create a personal account</title>
	<link rel="icon" type="image/png" href="<%= common.url %>assets/img/favicon.png" />
	<link href="<%= common.url %>assets/css/registration_controller.css?version=42" rel="stylesheet" />
</head>
<body>

	<a href="<%=common.url%>splash"> <img
		src="<%=common.url%>assets/img/logo.png" alt="logo" width="110"
		height="50"></a>
	<div class="member-signin">
		<span>Already a member? <a
			href="<%=common.url%>profile/controller/Sign_in_controller.jsp"><span
				class="signin">Sign in</span></a></span>

	</div>

	<div class="main-container">
		<h1>Create an account</h1>

		<div class="container-0">
			<div class="radio-buttons">
				<input type="radio" id="personal_account" name="account"
					value="personal_account" checked> <label
					for="personal_account">Personal account</label><br> <input
					type="radio" id="business_account" name="account"
					value="business_account" class="right-radio-btn"> <label
					for="business_account">Business account</label><br>
			</div>
		</div>

		<br>
		<div class="container-1">
			<div class="container-2">
				<br> <br> <br> <br>
				<!-- <form id="" modelAttribute="user" action="processForm" method="post" > -->
				<form id="" modelAttribute="user" action="splash" method="post">
					<div class="name-input">
						<input type="text" placeholder="First name"> <input
							type="text" placeholder="Last name">
					</div>
					<br>
					<div class="email-input">
						<input type="email" placeholder="Email">
					</div>
					<br>
					<div class="password-input">
						<input type="password" placeholder="Password">
					</div>
					<br>
					<div class="disclaimer">
						<p>
							By <strong>creating an account</strong>, you agree to our <a
								href="https://www.ebay.com/help/policies/member-behaviour-policies/user-agreement?id=4259"
								target="_blank"> User Agreement</a> and acknowledge reading our
							<a
								href="https://www.ebay.com/help/policies/member-behaviour-policies/user-privacy-notice-privacy-policy?id=4260"
								target="_blank"> User Privacy Notice.</a>
						</p>
					</div>
					<div class="registration">
						<input type="button" value="Create account"
							class="registration-btn">
					</div>
				</form>
			</div>

			<div class="container-3">
				<hr class="horizontal-rule">
				<p>or</p>
				<hr class="horizontal-rule">
			</div>

			<div class="container-4">
				<p>Alternative sign up methods</p>
			</div>

		</div>

	</div>


</body>
</html>