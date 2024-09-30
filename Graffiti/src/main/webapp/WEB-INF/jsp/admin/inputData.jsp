<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>inputData</title>
</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<h3>File Upload:</h3>
	Select a file to upload:
	<br />
	<form action="inputData" method="post" enctype="multipart/form-data">
		<input type="file" name="file" /> <br /> <input type="submit"
			value="Upload File" /> <input type="hidden"
			name="${_csrf.parameterName}" value="${_csrf.token}"
			class="form-control" />
	</form>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>