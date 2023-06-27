<%@page import="bean.common"%>
<%@page import="bean.Login_Bean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="icon" type="image/png" href="<%= common.url %>assets/img/favicon.png" />
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
	
	<a href="<%= common.url %>ebay.com">
		<img src="<%= common.url %>assets/img/logo.png" alt="eBay logo" width="110" height="50">
	</a>
	
	<div>
		<h1>Profile Page</h1>
		<table border="1">
			<tr>
<!-- 				<td><a href="Home.jsp">Home</a></td> -->
<!-- 				<td><a href="My_Profile.jsp">Profile</a></td> -->

<!-- 				<td><a href="Home.jsp">Home</a></td> -->
				<td><a href="http://localhost:8080/eBay/ebay.com">Home</a></td>
				
<!-- 				<td><a href="My_Profile.jsp">Profile</a></td> -->
				<td><a href="http://localhost:8080/eBay/profile">Profile</a></td>
				<td>Welcome <%=obj_Login_Bean.getUser_name() %></td>
				
				<td>Welcome <%=obj_Login_Bean.getUser_name() %></td>
				<td><a href="http://localhost:8080/eBay/signoutcontroller">Log Out</a></td>
		</table>
	</div>

</body>
</html>