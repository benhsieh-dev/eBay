<%@page import="modal.Login_Modal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<jsp:useBean id="obj_Login_Bean" class="bean.Login_Bean"></jsp:useBean>
	<jsp:setProperty property="*" name="obj_Login_Bean" />

	<%
		System.out.println(obj_Login_Bean.getUser_name());
	System.out.println(obj_Login_Bean.getPassword());

	Login_Modal obj_Login_Modal = new Login_Modal();

	boolean flag = obj_Login_Modal.check_user_name(obj_Login_Bean);

	if (flag) {

		session.setAttribute("user_session", obj_Login_Bean);
	%>

	<script type="text/javascript">
		window.location.href = "http://localhost:8080/eBay/ebay.com/<%=obj_Login_Bean.getUser_name()%>";
	</script>

	<%
		} else {

	session.setAttribute("login message", "Login Failed, username and password are wrong");
	%>

	<script type="text/javascript">
		window.location.href = "http://localhost:8080/eBay/index.jsp";
	</script>

	<%
		}
	%>

</body>
</html>