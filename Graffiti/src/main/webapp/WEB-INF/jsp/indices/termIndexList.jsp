<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
	.index {
		color:black;
		padding-left:5px; 
		padding-top:4px;
		margin:-5px 15% -5px auto; 
		width:70%;
		font-size:20;
		background-color:silver;
		border-radius:4px;
		cursor: pointer;
	}
	
	.silentHover:hover{
		text-decoration: none;
	}
	.altIndex {
		 background-color:lightgrey;
	}
	
	.index:hover{
		background-color: #999898;
		text-decoration: none;
	}
	.count{
		float:right;
		padding-right:5px;
	}
	.switch-field {
		display: flex;
		overflow: hidden;
	}
	
	.switch-field input {
		position: absolute !important;
		clip: rect(0, 0, 0, 0);
		height: 1px;
		width: 1px;
		border: 0;
		overflow: hidden;
	}
	
	.switch-field label {
		background-color: #303030;
		color: white;
		font-size: 14px;
		line-height: 1;
		text-align: center;
		padding: 8px 16px;
		margin-right: -1px;
		border: 1px solid black;
		box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px rgba(255, 255, 255, 0.1);
		transition: all 0.1s ease-in-out;
	}
	
	.switch-field label:hover {
		cursor: pointer;
	}
	
	.switch-field input:checked + label {
		background-color: maroon;
		box-shadow: none;
	}
	
	.switch-field label:first-of-type {
		border-radius: 4px 0 0 4px;
	}
	
	.switch-field label:last-of-type {
		border-radius: 0 4px 4px 0;
	}
	
table{ 
	width: 50%; 
	margin-left: 25%; 
	margin-right: 25%; 
	
} 

thead {
	border: 1px solid #ddd;
	background-color: white;
}

td{
	border: 1px solid #ddd;
}

th {
	font-weight: bold;
	color: maroon;
	border: 1px solid #ddd;
}

.term {
	padding-left: 10pt; 
}


.figural{
	display: none;
}
.odd{
	background-color: #f9f9f9;
}

.even{
	background-color: #ffffff;
}

</style>
<%-- Functions for sorting the values --%>

<%@	page import="edu.wlu.graffiti.bean.IndexTerm"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>
<% 
	java.util.List<IndexTerm> terms = (java.util.List<IndexTerm>)request.getAttribute("terms");
%>

<div name="indicesNav" style="text-align:center; margin-left:25%; margin-right:25%; width: 50%;">

	<p>These indices represent a best effort attempt to catalog the terms contained within the inscriptions in the AGP database, 
		therefore 100% accuracy is not guaranteed</p>

	<div class="switch-field" style="display:inline-flex">
		<input type="radio" id="terms" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/terms'"/>
		<label for="terms">Terms</label>
		<input type="radio" id="people" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/people'"/>
		<label for="people">People</label>
		<input type="radio" id="places" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/places'"/>
		<label for="places">Places</label>
		<input type="radio" id="symbol" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/symbol'"/>
		<label for="symbol">Symbols</label>
		<input type="radio" id="figural" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/figural'"/>
		<label for="figural">Figural</label>
		<input type="radio" id="figural-terms" name="switch-two" onclick="window.location.href='<%=request.getContextPath()%>/indices/figural-terms'"/>
		<label for="figural-terms">Figural-Terms</label>
	</div>
	
	<p>Entries: <%=terms.size()%></p>
	
	<% String sort = (String)request.getParameter("sort_by");%>
	
	<%if (sort == null || sort.equals("alpha")){ %>
	
		<% String index = (String)request.getAttribute("index");%>
		<% String lang_param =  (String)request.getParameter("lang");
		List<String> lang = null;
		if (lang_param != null){
			lang = Arrays.asList((lang_param).split(" "));
		}
		%>
		
		
		<div name="letterBookmarks" style="display:inline-block" class="jumpto">
			<p style="font-size: 20">
				Jump to:
				<% if (lang == null || lang.contains("latin")||lang.contains("greek-latin")){ %>
					<% if (index.equals("people") || index.equals("places")){ %>
						<!-- Create Jump To for Capital Latin Alphabet -->
						<br/>
						<% for(int j=0;j<26;j++){%>
							<a href="#<%=(char)(j+'A') %>">
								<%=(char)(j+'A') %>
							</a>
						<%}%>
					<%} %>
					<% if (!index.equals("people") && !index.equals("places")){ %>
						<!-- Create Jump To for Lower Case Latin Alphabet -->
						<br/>
						<% for(int j=0;j<26;j++){%>
							<a href="#<%=(char)(j+'a') %>">
								<%=(char)(j+'a') %>
							</a>
						<%}%>
					<%}%>
				<%} %>
				<% if (lang == null || lang.contains("greek") || lang.contains("greek-latin")){ %>
					<% if (index.equals("people") || index.equals("places")){ %>
						<!-- Create Jump To for Capital Greek Alphabet -->
						<br/>
						<% for(int j=0;j<25;j++){
							if (j+913 != 930){%>
							<a href="#&#<%=(j+913) %>;">
								&#<%=(j+913)%>;
							</a>
						<%}}%>
					<%} %>
					<% if (index.equals("terms")){ %>
						<!-- Create Jump To for Lower Case Greek Alphabet -->
						<br/>
						<% for(int j=0;j<25;j++){%>
							<a href="#&#<%=(j+945) %>;">
								&#<%=(j+945)%>;
							</a>
						<%}%>
					<%}%>
				<%}%>
			</p>
		</div>
	<%} 
	else{%>
		<br/>
		<br/>
	<%} %>
</div>

<%-- Creates a table for the index terms --%>


<table>
	<thead>
		<tr>
			<th class="term">Term</th>
			<th style="text-align:center;">Appearances</th>
		</tr>
	</thead>
	<tbody>
	<% 
		
		for(int i=0;i<terms.size();i++){
		
		IndexTerm previous = null;
		IndexTerm current = terms.get(i);
		if (i > 0){
			previous = terms.get(i-1);
		}%>
			<tr class="index altIndex silentHover 
				<%if(i%2 == 0){%> 
					<%= "odd"%> 
				<%}else{%>
					<%= "even"%>
				<%}%>				
<%-- 				<%if (current.getDisplay()){%> --%>
<%-- 					<%="display"%> --%>
<%-- 				<%}else{%> --%>
<%-- 					<%= "no-display"%> --%>
<%-- 				<%} %>  --%>
				" 
				<%String link = request.getContextPath();
				String city_param =  (String) request.getParameter("city");
				link += "/indices/";
				link += request.getAttribute("index");
				link += "/term?id=";
				link += terms.get(i).getTermID();
				if (city_param!=null){
					link += "&city=";
					link += city_param.replace(" ", "+");
				}%>
				onclick="location.href='<%=link%>'">
				<td class="term" id="<%=terms.get(i).getTermID()%>">
					<!-- Places anchors for alphabetic searching -->
					<% if (previous != null && previous.getTerm().length() > 0 && !previous.getTerm().substring(0,1).equals(current.getTerm().substring(0, 1))){%>
						<a name="<%=current.getTerm().substring(0, 1)%>"></a>
					<%} %>
					<a href="<%=link%>"><%=current.getTerm()%></a>
				</td>
				<td class="appearances" style="text-align:center; width:20%;">
					<%
					int count;
					if (request.getParameter("city") instanceof String){
						String cityparam =  (String) request.getParameter("city");
						List<String> items = Arrays.asList(cityparam.split(" "));
						count = terms.get(i).getEntriesByLocation(items).size();
					}
					else{
						count = terms.get(i).getEntries().size();
					}
					%>
					<%=count%>
				</td>
			</tr>		
	<%} %>
	</tbody>	
</table>

