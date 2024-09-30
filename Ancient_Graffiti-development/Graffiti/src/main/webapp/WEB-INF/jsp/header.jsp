<!-- The header that is displayed at the top of every page -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!--<fmt:setLocale value="${param.locale}" scope="session" />-->

<header id="top" role="banner">
	<nav class="navbar navbar-expand-lg navbar-dark fixed-top">
		<div class="container-fluid">
			<!-- Create AGP that is always visible on the header -->
			<a class="navbar-brand" href="<%=request.getContextPath()%>/">AGP</a>

			<!-- Create the collapse / uncollapse button -->
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#myNavbar"
				aria-controls="myNavbar" aria-expanded="false"
				aria-label="Toggle navigation" style="width: 45px;"
				id="navbar-collapser">
				<span class="navbar-toggler-icon"></span>
			</button>

			<!-- Create the collapsable navbar -->
			<div class="collapse navbar-collapse" id="myNavbar">
				<!-- Populate the Navbar with different dropdowns -->
				<ul class="navbar-nav me-auto mb-2 mb-lg-0 mx-1" id="nav">
					<!-- Browse Dropdown -->
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						data-bs-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"><fmt:message key="nav.browse" /></a>


						<div class="dropdown-menu"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/results"><fmt:message
									key="nav.allgraf" /></a> 
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/results?drawing_category=All"><fmt:message
									key="nav.figs"/></a>
						</div></li>


					<!-- Map Search Dropdown -->
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						data-bs-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"><fmt:message key="nav.maps" /></a>

						<div class="dropdown-menu"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/searchHerculaneum"><fmt:message
									key="herculaneum" /></a> <a class="dropdown-item"
								href="<%=request.getContextPath()%>/searchPompeii"><fmt:message
									key="pompeii" /></a>
						</div></li>

					<!-- Featured Graffiti -->
					<li class="nav-item"><a class="nav-link"
						href="<%=request.getContextPath()%>/featured-graffiti"><fmt:message
								key="nav.featured" /></a></li>

					<!-- Resources Dropdown -->
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						data-bs-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"><fmt:message key="nav.resources" /></a>

						<div class="dropdown-menu"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item"
								href="/about/main/epigraphic-conventions"><fmt:message
									key="nav.conventions" /></a> <a class="dropdown-item"
								href="<%=request.getContextPath()%>/properties"><fmt:message
									key="nav.props" /></a> <a class="dropdown-item"
								href="<%=request.getContextPath()%>/streets"><fmt:message
									key="nav.streets" /></a> <a class="dropdown-item"
								href="/about/teaching-resources/"> <fmt:message
									key="nav.teaching" /></a>

							<!--  commenting out swagger for now -->
							<!-- 
							<a class="dropdown-item"
							href="<%=request.getContextPath()%>/swagger-ui.html"> API
							Documentation </a>
							 -->
						</div></li>

					<!-- About the Project Dropdown -->
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="/about" id="navbarDropdown"
						data-bs-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"><fmt:message key="nav.about" /></a>

						<div class="dropdown-menu"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item" href="/about"><fmt:message
									key="nav.overview" /></a> <a class="dropdown-item"
								href="/about/teams/"><fmt:message key="nav.teams" /></a> <a
								class="dropdown-item" href="/about/fieldwork/"><fmt:message
									key="nav.fieldwork" /></a> <a class="dropdown-item"
								href="/about/main/funding/"><fmt:message key="nav.funding" /></a>
						</div></li>

					<%
					boolean isAdmin = false;
					if (session.getAttribute("authenticated") != null) {
						isAdmin = (Boolean) session.getAttribute("authenticated");
					}

					/* If user is authenticated, Admin menu appears. */
					if (isAdmin) {
					%>
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle"
						href="<%=request.getContextPath()%>/admin" id="navbarDropdown"
						data-bs-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false">Admin</a>

						<div class="dropdown-menu"
							aria-labelledby="navbarDropdownMenuLink">
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/admin">Home</a> <a
								class="dropdown-item"
								href="<%=request.getContextPath()%>/logout">Logout</a>
						</div></li>


				</ul>
				<%
				}
				%>

				<!-- Search Bar -->
				<form class="d-flex mt-2 mt-lg-0" id="searchForm">
					<input id="globalSearch" type="text" class="form-control me-2"
						placeholder="<fmt:message key="search.text"/>">
					<button class="btn navbar-btn" id="navButton"
						onclick="globalSearchFromHeader();">
						<fmt:message key="search.button" />
					</button>
				</form>

				<!-- Language Option Button -->

				<form class="d-flex mt-2 mt-lg-0" action="<%=request.getContextPath()%>/lang" method="get">
					<select class="form-select m-2" name="lang" id="lang">
						<option value="en">English</option>
						<option value="es">Español</option>
						<!-- Add more languages here as needed -->
					</select>
					<button class="btn navbar-btn" type="submit"><fmt:message key="lang.change"/></button>
				</form>


			</div>


			<!--/.navbar-collapse -->
		</div>
	</nav>

	<!-- Main jumbotron  -->
	<div class="block" id="Jumbo" style="padding-top: 50px">
		<div class="jumbotron">
			<div class="container">
				<h1 style="padding-top: 15px">The Ancient Graffiti Project</h1>
				<p>
					<fmt:message key="jumbo.description" />
				</p>
			</div>
		</div>
	</div>

	<script type="text/javascript">
	function globalSearchFromHeader() {
		var param = document.getElementById("globalSearch").value;
		if(param != "") {
			param = param.replace(" ", "_");
			var url ="<%=request.getContextPath()%>";
				url += "/results?global=" + param + "&sort_by=relevance";
				window.open(url);
			}
		}
	</script>
</header>
