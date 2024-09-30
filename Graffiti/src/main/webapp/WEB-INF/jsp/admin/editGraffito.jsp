<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Edit Graffito</title>
<%@include file="/resources/common_head.txt"%>
<script>
		function checkID(){
			
			if(document.forms["edit"].graffitiID.value == null || document.forms["edit"].graffitiID.value == ""){
				alert("Please enter in a graffiti ID to edit");
				return false;
			}
			else{
				return true;
			}
		}
			
		</script>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<h2>Edit Graffito</h2>
		
		<c:if test="${requestScope.msg != null }">
			<p class="alert alert-danger" role="alert">${requestScope.msg}</p>
		</c:if>

		
		<form name= "edit" id="edit" class="form-inline" action="updateGraffito" method="GET" onsubmit= "return checkID();">
			<input type="text" name="graffitiID" placeholder="Graffiti ID" size="20" />
			<input type="submit" class="btn btn-agp" value="Edit" />
		</form>
		

	</div>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>