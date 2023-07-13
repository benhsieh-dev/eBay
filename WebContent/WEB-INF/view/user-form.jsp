<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

		<div>
			<form:form action="processForm" modelAttribute="user">
				First name: <form:input path="firstName" />
				<br><br>
				Last name (*): <form:input path="lastName" />
				<form:errors path="lastName" cssClass="error" />
				<br><br>
				Email (*): <form:input path="email" />
				<form:errors path="email" cssClass="error" />
				<br><br>
				Password (*): <form:input path="password" />
				<form:errors path="password" cssClass="error" />
				<br><br>
				<input type="submit" value="Submit" />
			</form:form>
			
		</div>

</body>
</html>