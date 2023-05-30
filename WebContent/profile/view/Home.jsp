<%@page import="bean.Login_Bean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<%
		Login_Bean obj_Login_Bean=(Login_Bean) session.getAttribute("user_session"); 

		if(obj_Login_Bean == null) {
			session.setAttribute("login message", "Please login first"); 
	%>
					
			<script type="text/javascript">
				window.location.href="http://localhost:8080/eBay/index.jsp";
			</script>	
		
	<% 	
		}
	%>
	<center>
		<h1>Home Page</h1>
		
		<table border="1">
			<tr>
				<td><a href="Home.jsp">Home</a></td>
				<td><a href="My_Profile.jsp">Profile</a></td>
			</tr>
		</table>
	</center>

</body>
</html>