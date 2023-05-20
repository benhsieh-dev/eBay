<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<jsp:useBean id="obj_Login_Bean" class="bean.Login_Bean" ></jsp:useBean>
	<jsp:setProperty property="*" name="obj_Login_Bean"/>
	
	<%
		System.out.println(obj_Login_Bean.getUser_name());
		System.out.println(obj_Login_Bean.getPassword());
	%>
</body>
</html>