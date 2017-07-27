<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>

<head>
<title>Customer Information</title>
</head>

<body>
	<h2>Customer Information</h2>
	<hr>
	<form:form method = "POST" action = "classifyResult">
         <table>
            <tr>
               <td><form:label path = "id">Customer Id</form:label></td>
               <td><form:select path="id" items="${customerIds}"/></td>
            </tr>
            <tr>
               <td><form:label path = "month">Month</form:label></td>
               <td><form:select path="month" items="${months}"/></td>
            </tr>
            <tr>
               <td><form:label path = "year">Year</form:label></td>
               <td><form:select path="year" items="${years}"/></td>
            </tr>
            <tr>
               <td colspan = "2">
                  <input type = "submit" value = "Submit"/>
               </td>
            </tr>
         </table>  
      </form:form>
</body>
</html>