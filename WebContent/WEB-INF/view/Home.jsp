<%@page import="bean.Login_Bean"%>
<%@page import="bean.common"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<!-- 	<link href="http://localhost:8080/logo/assets/css/home.css" rel="stylesheet" /> -->
		<link href="<%= common.url %>assets/css/home.css?version=17" rel="stylesheet" />
<%-- 		<link rel="icon" type="image/x-icon" href="<%= common.url %>assets/img/favicon.png" /> --%>
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
	<div>
	
	<div class="navigation-menu">
		<div class="left-navigation">
			<div>Hi <strong><%=obj_Login_Bean.getUser_name() %>!</strong></div>
			<div>Daily Deals</div>
			<div>Brand Outlet</div>
			<div>Help & Contact</div>
		</div>
	
		<div class="right-navigation">
			<div>Sell</strong></div>
			<div>Watchlist</div>
			<div>My eBay</div>
		</div>
	</div>
	
	<hr>
	
	
	
	<img src="<%= common.url %>assets/img/logo.png" alt="eBay logo" width="110" height="50">
		
		<table border="1">
			<tr>
<!-- 				<td><a href="Home.jsp">Home</a></td> -->
				<td><a href="http://localhost:8080/eBay/ebay.com">Home</a></td>
<!-- 				<td><a href="My_Profile.jsp">Profile</a></td> -->
				<td><a href="http://localhost:8080/eBay/profile">Profile</a></td>
				<td>			
<!-- 					<a href="../controller/Sign_out_controller.jsp">Log Out</a> -->
					<a href="http://localhost:8080/eBay/signoutcontroller">Log Out</a>
				</td>
			</tr>
		</table>
	</div>

</body>
</html>