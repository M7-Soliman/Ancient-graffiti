<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="/resources/common_head.txt"%>
<style type="text/css">
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.leftcol {
	float: left;
	width: 50%;
	margin-bottom: 25px;
}

.rightcol {
	float: right;
	width: 50%;
	margin-bottom: 25px;
}

li {
	padding: 2px;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>
</head>

<body>
	<%
		String role = (String) session.getAttribute("role");
	%>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">
		<%
			String msg = "";
			if (request.getAttribute("msg") != null) {
				msg = (String) request.getAttribute("msg");
		%>
		<div class="alert alert-info" role="alert">
			<%=msg%>
		</div>
		<%
			}
		%>
	</div>
	<div class="container">
	
		<h2>
			Welcome,
			<%=session.getAttribute("name")%>!
		</h2>
		
		<div class="row">
		<div class="col-sm-6">
		  <div class="card bg-light mb-3">
		     <div class="card-body">
		      <h4 class="card-title">Your Account</h4>
				<ul class="list-group">
					<li class="list-group-item ">
						<a href="<%=request.getContextPath()%>/admin/changePassword">Change Password</a>
					</li>
				</ul>
		   	 </div>
		  </div>
		  </div>
		  <div class="col-sm-6">
		    <div class="card bg-light mb-3">
		      <div class="card-body">
		        <h4 class="card-title">Content</h4>
				<ul class="list-group">
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/figuralCaptionOccurrencesReport">View Figural Caption Occurrences Report</a></li>
			
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/figuralTermsReport">View Figural Terms Occurrences Report</a></li>
			
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/indices/indices">Indices Page</a></li>
				</ul>
		      </div>
		    </div>
		  </div>
		</div>
		
		<div class="row">
		<div class="col-sm-6">
		  <div class="card bg-light mb-3">
		     <div class="card-body">
		      <h4 class="card-title">Other Data Reports</h4>
				<ul class="list-group">
		
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportUnidentifiedFigural">View Unrecognized Figural Report</a></li>
					
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportMissingFiguralData">View Figural Missing Data Report</a></li>
					
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportMissing">View Textual Missing Data Report</a></li>
					
					
										<!-- Moc design for poetry filter implementation -->
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportUnconfirmedPoetry">View Unconfirmed Poetry Report</a></li>
					
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportMissingFindspot">View Missing Findspot Report</a></li>
					
				
				</ul>
		   	 </div>
		  </div>
		  </div>
		  <div class="col-sm-6">
		    <div class="card bg-light mb-3">
		      <div class="card-body">
		        <h4 class="card-title">Reports</h4>
				<ul class="list-group">
		
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportMissingLangner">View Langner Entries Report</a></li>
					
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportAllFigural">View All Figural Report</a></li>
					
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportAllTxt">View All Textual Report</a></li>
					
					<!-- Moc design for poetry filter implementation -->
					<li class = "list-group-item"><a href = "<%=request.getContextPath()%>/admin/reportConfirmedPoetry">View Confirmed Poetry Report</a></li>
					
					<!-- Not currently working for all graffiti report, returns 404-->
					<li class="list-group-item"><a href="<%=request.getContextPath()%>/admin/report?query_all=true?query_all=true">View Report for all Graffiti</a></li>
					
				</ul>
		      </div>
		    </div>
		  </div>
		</div>
					
		<div class="row">
		<div class="col-sm-6">
		  <div class="card bg-light mb-3">
		     <div class="card-body">
		      <h4 class="card-title">Downloads</h4>
				<ul class="list-group">	
		
					<li class="list-group-item ">
						<a href="<%=request.getContextPath()%>/all/xml">
							Download All Graffiti as Epidoc <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
		  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
		  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
		</svg>
						</a>
					</li>
					<li class="list-group-item">
						<a href="<%=request.getContextPath()%>/lemmaTable/csv">
							Download Lemma Table <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
		  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
		  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
		</svg>
						</a>
					</li>
					<li class="list-group-item ">
						<a href="<%=request.getContextPath()%>/posTable/csv">
							Download Index Part of Speech Table <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
		  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
		  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
		</svg>
						</a>
					</li>
				</ul>
				<!--  
				<li class="list-group-item">
					<a href="<%=request.getContextPath()%>/figuralTable/csv">
						Download Report for Figural Graffiti <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
	  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
	  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
	</svg>
					</a>
				</li>
				<li class="list-group-item ">
					<a href="<%=request.getContextPath()%>/missingTable/csv">
						Download Report for Missing Data <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
	  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
	  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
	</svg>
					</a>
				</li>
				<li class="list-group-item">
					<a href="<%=request.getContextPath()%>/langnerTable/csv">
						Download Report of Langner Numbers for Figural Graffiti <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
	  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
	  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
	</svg>
					</a>
				</li>
				<li class="list-group-item ">
					<a href="<%=request.getContextPath()%>/textualTable/csv">
						Download Report for Textual Graffiti <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
	  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
	  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
	</svg>
					</a>
				</li>
				<li class="list-group-item ">
					<a href="<%=request.getContextPath()%>/missingfiguralTable/csv">
						Download Report for Missing Figural Graffiti <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
	  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
	  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
	</svg>
					</a>
				</li>
				-->	
		   	 </div>
		  </div>
		  </div>
		<%
			if (role != null && role.equals("admin")) {
		%>
		  <div class="col-sm-6">
		    <div class="card bg-light mb-3">
		      <div class="card-body">
		        <h4 class="card-title">Users and Indexing</h4>
				<ul class="list-group">
					<li class="list-group-item "><a href="<%=request.getContextPath()%>/admin/addEditor">Add
							a new Editor</a></li>
					<li class="list-group-item "><a href="<%=request.getContextPath()%>/admin/RemoveEditors">Remove
							Editor</a></li>
					<li class="list-group-item "><a href="<%=request.getContextPath()%>/admin/modifyIgnoreList">Modify Ignore List</a></li>
				</ul>
		      </div>
		    </div>
		  </div>
		<%
			}
		%>
		</div>	
		
		
	</div>
	<%@include file="/WEB-INF/jsp/footer.jsp"%>
</body>
</html>