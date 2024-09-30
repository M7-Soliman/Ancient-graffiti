<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="/resources/common_head.txt"%>
<meta charset="UTF-8">
<title>Ancient Graffiti Project :: Error</title>
</head>
<body>
	<%@include file="../header.jsp"%>

	<div class="container" style="max-width: 1140px;">
		

		<h2>Oh no!  Something went wrong!</h2>
		<p></p>
		<a href="<%=request.getContextPath()%>/">
				<button class="btn btn-agp right-align">Return to Home Page</button>
			</a>
				
	</div>
	<%@include file="../footer.jsp"%>
</body>
</html>