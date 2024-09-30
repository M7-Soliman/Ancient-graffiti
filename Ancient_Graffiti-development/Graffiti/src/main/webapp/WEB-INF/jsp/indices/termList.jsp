<style>
table{
	width: 50% !important;
	margin-left: 25% !important;
	margin-right: 25% !important;
}

.odd{
	background-color: #f9f9f9;
}

.even{
	background-color: #ffffff;
}

th {
	font-weight: bold;
	color: maroon;
	border: 1px solid #ddd;
}

#entries {
	text-align: center;
}
</style>

<script type="text/javascript">
// 	function selectAll(){
// 		var allElements = document.getElementsByClassName("silentHover");
// 		for(var i=0;i<allElements.length;i++){
// 			allElements[i].style.display="block";
			
// 		}
// 	}
// 	function selectPeople(){
// 		var people = document.getElementsByClassName("person");
// 		var places = document.getElementsByClassName("place");
// 		for(var i=0;i<people.length;i++){
// 			people[i].style.display="block";
// 			if(i%2==1){
// 				people[i].getElementsByClassName("index")[0].style.backgroundColor="silver";
// 			}
// 			else{
// 				people[i].getElementsByClassName("index")[0].style.backgroundColor="lightgrey";
// 			}
// 		}
// 		for(var j=0;j<places.length;j++){
// 			places[j].style.display="none";
			
			
// 		}
// 	}
// 	function selectPlaces(){
// 		var people = document.getElementsByClassName("person");
// 		var places = document.getElementsByClassName("place");
// 		for(var i=0;i<people.length;i++){
// 			people[i].style.display="none";
			
// 		}
// 		for(var j=0;j<places.length;j++){
// 			places[j].style.display="block";
// 			if(j%2==1){
// 				places[j].getElementsByClassName("index")[0].style.backgroundColor="silver";
// 			}
// 			else{
// 				places[j].getElementsByClassName("index")[0].style.backgroundColor="lightgrey";
// 			}
			
// 		}
// 	}
</script>
<style>

</style>
<%@	page import="edu.wlu.graffiti.bean.IndexTerm"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.List"%>
<%IndexTerm term = (IndexTerm) request.getAttribute("term");%>

<h2 class="head" style="text-align: center;">Graffiti Including: <%=term.getTerm()%></h2>
<p id="entries">Entries: <%=term.getEntries().size()%></p>
<table class="table table-striped table-bordered">
<thead>
	<tr>
		<th>Graffito ID</th>
		<th>Ancient City</th>
		<th>Content</th>
	</tr>
</thead>
<tbody>
<% /* Used by the filter and stable URIs to display results */
	String city_param = null;
	List<String> items = null;
	if (request.getParameter("city") instanceof String){
		city_param = (String) request.getParameter("city");
		items = Arrays.asList(city_param.split(" "));
	}

	for(int i=0;i<term.getEntries().size();i++){
		if (city_param == null || items.contains(term.getEntries().get(i).getCity())){%>
		<tr class="index altIndex <%if(i%2 == 0){%> <%= "odd"%> <%}else{%><%= "even"%><%}%>">
			<td>
				<a href="<%=request.getContextPath()%>/graffito/AGP-<%= term.getEntries().get(i).getGraffitiID()%>">
					AGP-<%=term.getEntries().get(i).getGraffitiID()%>
				</a>
			</td>
			<td><%=term.getEntries().get(i).getCity()%></td>
			<td><%=term.getEntries().get(i).getContent()%></td>
		</tr>
		<%}}%>
</tbody>
</table>

