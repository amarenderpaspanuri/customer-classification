<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>

<head>
<title>Customer Classification</title>
</head>

<body>
	<form:form method = "GET" action = "/classifyForm">
		<table>
		   <tr>
		      <td>
		         <input type = "submit" value = "Back"/>
		      </td>
		   </tr>
		</table>  
	</form:form>
	<hr>
	
	<b>Current Balance :</b> ${balance}
	
	<hr>
	
	<c:if test="${not empty classifications}">
		<b>Classifications :</b>
		<ul>
			<c:forEach var="classification" items="${classifications}">
				<li>${classification}</li>
			</c:forEach>
		</ul>
		<hr>
	</c:if>
	
	<c:if test="${not empty transactions}">
		<table>
			<tr>
				<th>Amount in $</th>
				<th>Transaction Date/Time</th>
				<th>Description</th>
				<th>Transaction Type</th>
			</tr>
			<c:forEach items="${transactions}" var="transaction">
				<tr>
					<td>${transaction.amount}</td>
					<td>${transaction.time}</td>
					<td>${transaction.description}</td>
					<td>${transaction.type}</td>
				</tr>
			</c:forEach>
		</table>	
	</c:if>
</body>

</html>