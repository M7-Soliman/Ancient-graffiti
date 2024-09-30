<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mobile Page</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<%@ page import="java.util.List,java.util.ArrayList"%>
</head>
<body>
    <%@include file="header.jsp"%>

<div class="container-fluid">
      <div class="row">
        
        <div class="col-md-4" style="background-color:yellow;">
          Filters will go here! -- add col
        </div>

        <div class="col-md-8" style="background-color:lightblue; height: 400px;"> 
          Add maps in here!
        </div>
        
<!--         <div class="col-md-8" style="background-color:orange; flex-grow: 1;">
          ... results found
        </div>
        
        <div class="col-md-8" style="background-color:lightgray; flex-grow: 1;">
          Pagination
        </div> -->

        <div class="col col-md-8" style="background-color:pink; flex-grow: 1;height: 800px;">
          Results
        </div>

      </div>
</div>

</body>
</html>