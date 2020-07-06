<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        
<!DOCTYPE html>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<c:if test="${medlist ==null }">
<script type="text/javascript">
$(document).ready(function(){
	$("#issue").hide();
	$("#issue1").click(function(){
		$("#issue").show();
	});
	
});
</script>
</c:if>
<meta charset="ISO-8859-1">
<%  String pageTitle="Patient Billing";%>
<jsp:include page="header.jsp" flush="true">
<jsp:param value="<%=pageTitle %>" name="pagetitle"/>
</jsp:include>
<style type="text/css">
th{background-color:#3d3d5c;color:white;}
</style>
</head>
<body style="background-color:#339BFF; ">
<center>
<h2>Issue Medicine</h2>


<table border="1">
<tr>
<th>Patient Id</th><th>Name</th><th>Age</th><th>Address</th><th>DOJ</th><th>Type of Room </th>
</tr>
<tr>
<td><form action="/get-patient-detail/2" method="get">
<input type="text" name="searpatientId" value="${patients.patientId }" required >
<input type="submit" value="search">
</form></td>
<c:if test="${patients!=null }"><td>${patients.pname}</td> </c:if>
<c:if test="${patients!=null }"><td> ${patients.age }</td></c:if>
<c:if test="${patients!=null }"><td>${patients.address }</td></c:if>
<c:if test="${patients!=null }"><td>${patients.dateOfAdd }</td></c:if>
<c:if test="${patients!=null }"><td>${patients.bedtype }</td></c:if>
</tr>
</table>

<br>
<br>
<c:if test="${not empty plist1}">
<h4>Issued Medicine</h4>
<table border="1">
<tr>
<th>Medicine</th><th>Quantity</th><th>Rate</th><th>Amount</th><th>Action</th>
</tr>
<c:forEach items="${plist1}" var="p">

<tr>
<td>${p.medName }</td>
<td>${p.quantity}</td>
<td>${p.rateOfMed }</td>
<td>${p.amount }</td>
<td><a href="/del-patient-med/${p.pat_med_id}"> Delete</a></td>
 </tr>
</c:forEach>
</table>

</c:if>
<br>
<br>
<button id="issue1">Issue Medicine</button>
<br>
<br>
<div id="issue" >
<table border="1">
<tr>
<th>Medicine</th><th>Quantity</th><th>Rate</th><th>Amount</th>
</tr>


<tr>
<td><form  method="get">
<input type="hidden" name="searpatientId" value="${patients.patientId }" >
<input type="text" name="getmed" value="${medlist.medicineName}">
<input type="submit" value="meds"  id="submit">
</form></td>
<td><input type="text" name="quan" oninput="myfun()" id="quant"></td>
<td>${medlist.rateOfMed}</td>
<td><input type="text" name="amt" id="price"> </td>
 </tr>
</table>
<form action="/get-patient-detail/2" method="get">
<input type="hidden" name="searpatientId" value="${patients.patientId }" >
<input type="hidden" name="getmed" value="${medlist.medicineName}">
<input type="hidden" name="quan" id="quan1">
<input type="hidden" name="amt" id="price1">
<input type="submit" value="ADD">
</form>
<button><a href="/get-patient-detail/2?searpatientId=${patients.patientId }">Refresh</a></button>
</div>
<br>
<h4>${msg}</h4>
<h4>${msg1}</h4>
</center>
<script type="text/javascript">
function myfun(){

var rate=${medlist.rateOfMed};
var quan=document.getElementById('quant').value;
document.getElementById('price').value=rate*quan;
document.getElementById('quan1').value=quan;
document.getElementById('price1').value=rate*quan;
}
</script>

</body>
</html>