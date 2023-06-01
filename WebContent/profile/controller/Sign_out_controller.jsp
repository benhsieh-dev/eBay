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

	<jsp:useBean id="obj_Login_Bean" class="bean.Login_Bean" ></jsp:useBean>
	<jsp:setProperty property="*" name="obj_Login_Bean"/>
<%

/* 	Login_Modal obj_Login_Modal = new Login_Modal();

	boolean flag = obj_Login_Modal.check_user_name(obj_Login_Bean);
	
	if (flag) { */
		session.removeAttribute("user_session");
		session.setAttribute("login message", "Signout successful"); 
%>

	<script type="text/javascript">
		window.location.href="http://localhost:8080/eBay/index.jsp";
	</script>
<%-- <%
	} else {
		session.setAttribute("login message", "Login Failed, username and password are wrong"); 
		
		%>
		
			<script type="text/javascript">
				window.location.href="http://localhost:8080/eBay/profile/view/Home.jsp";
			</script>
		
		<%
	}

%>	 --%>
		

</body>
</html>