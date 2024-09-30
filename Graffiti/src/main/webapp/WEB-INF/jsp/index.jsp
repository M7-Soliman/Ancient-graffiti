<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<!--<fmt:setLocale value="${param.locale}" scope="session"/>-->
<fmt:setBundle basename="messages"/>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="../../resources/common_head.txt"%>
<style>


.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.footer-img {
	display: inline-block;
	margin-left: 10px;
	margin-right: 10px;
	width: 110px;
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

h3 {
	text-align: center;
}

.alert-info {
	color: black;
	background-color: beige;
	border-color: lightgray;
	background-image: none;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}

#titles {
	font-size: 20px;
	margin-bottom: 5px;
}

#content {
	font-size: 16px;
	break-inside: avoid;
}

a {
	color: #113CFE;
}

#benefiel {
	color: maroon;
	text-align: center;
	font-size: 18px;
	font-weight: bold;
}

#benefiel a {
	color: black;
}

#benefiel a:hover {
	color: inherit;
}

#searchButton {
	background: none;
	color: #113CFE;
	font-style: normal;
	border: none;
	padding: 0;
	font: inherit;
	cursor: pointer;
}

#trifold {
	text-align: justify;
	max-width: 1100px;
	margin: auto;
	clear: both;
}

#socials {
	text-align: right;
	margin-top: 0px;
	margin-bottom: 20px;
	font-style: normal;
	height: 32px;
}

.img_wrap {
    position: relative;
}

.img_wrap > img:hover {
-webkit-filter: brightness(70%);
}

.img_description_herculaneum {
	position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  line-height: 100%;
  color: white;
}

.img_description_pompeii {
position: absolute;
top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  line-height: 100%;
  color: white;
}
</style>

<script>
	function change_color() {
		var searchLink = document.getElementById("searchButton");
		searchLink.style.color = 'inherit';
		searchLink.style.textDecoration = 'underline';
		searchLink.style.fontStyle = 'normal';
	}

	function original_color() {
		var searchLink = document.getElementById("searchButton");
		searchLink.style.color = '#113CFE';
		searchLink.style.textDecoration = 'none';
		searchLink.style.fontStyle = 'normal';
	}
	
	function change_herculaneum_appearance() {
		var herculaneumText = document.getElementById('img_description_herculaneum');
		if (herculaneumText.style.display == 'none') {
			herculaneumText.style.display = 'block';
		} else {
			herculaneumText.style.display = 'none';
		}
	}
	
	function change_pompeii_appearance() {
		var pompeiiText = document.getElementById('img_description_pompeii');
		if (pompeiiText.style.display == 'none') {
			pompeiiText.style.display = 'block';
		} else {
			pompeiiText.style.display = 'none';
		}
	}

	function make_search_active() {
		var navDD2 = document.getElementById("myNavbar");
		if (navDD2.classList.contains("show") != true) {
			var navDropdown = document.getElementById("navbar-collapser");
			navDropdown.click();
		}
		searchBar = document.getElementById("globalSearch");
		searchBar.focus();
		document.getElementById("top").scrollIntoView();
	}
</script>
</head>
<body>
	<%@include file="header.jsp"%>
	<main role="main">
		<%-- when smyrna added, will probably need to move this into the trifold,
	or create a new trifold div depending on what happens --%>
		<div class="container"
			style="max-width: 1100px; margin: auto; margin-top: 20px;">
			<div class="leftcol">
				<h3><fmt:message key="herculaneum"/></h3>
				<!-- Sets up the method of reaching the interactive map of Herculaneum on the home page.
				This is simply a button that will take you to the interactive map on the next page. -->
				<a class="img_wrap" href="searchHerculaneum"><img class="fluid-img"
					style="max-height: 280px" alt="Map of Herculaneum properties"
					src="<%=request.getContextPath()%>/resources/images/Herculaneum.jpg"
					onmouseover="change_herculaneum_appearance()"
					onmouseout="change_herculaneum_appearance()" />
					<p id="img_description_herculaneum" class="img_description_herculaneum" style="display: none;"><fmt:message key="explore"/> <fmt:message key="herculaneum"/></p>
					</a>
			</div>

			<div class="rightcol">
				<h3> <fmt:message key="pompeii"/> </h3>
				<!-- Sets up the method of reaching the interactive map of Pompeii on the home page.
				This is simply a button that will take you to the interactive map on the next page. -->
				<a class="img_wrap" href="searchPompeii"><img class="fluid-img"
					style="max-height: 280px" alt="Map of Pompeii"
					src="<%=request.getContextPath()%>/resources/images/Pompeii.jpg"
					onmouseover="change_pompeii_appearance()"
					onmouseout="change_pompeii_appearance()" />
					<p id="img_description_pompeii" class="img_description_pompeii" style="display: none;"><fmt:message key="explore"/> <fmt:message key="pompeii"/></p></a>
			</div>
		</div>
		
		<!-- The following classes in the "container" class all give the user general information about
		the site. The container uses a trifold layout, allowing all three paragraphs to fit nicely on
		the user's screen. These three paragraphs contain basic information about graffiti, what
		the user can do to get started with the site, and how to access the more scholarly aspect of the
		site. 
		 -->
		<div class="container" style="margin: auto;">
			<div class="row" id="trifold">
				<div class="col-md" id="welcome">
					<h3 id="titles"><fmt:message key="title.graf"/></h3>
					<p id="content">
						<!-- Welcome to The Ancient Graffiti Project, a digital resource for
						locating and studying handwritten inscriptions of the early Roman
						empire. These ancient messages and sketches offer a window into
						the daily life and interests of the people who lived in the
						ancient world, especially in Herculaneum and Pompeii. They provide
						perspectives on Roman society, the ancient economy, religion,
						spoken language, literacy, and activities within the ancient city.
						<br> 
						<br>
						(N.B. The word "graffiti" was originally a
						technical term for ancient handwritten wall-inscriptions that were
						scratched into wall plaster. The term later came to mean any
						writing on a wall.) -->
						<fmt:message key="graf.pt1"/>
						<br>
						<br>
						<fmt:message key="graf.pt2"/>
					</p>
				</div>
				<div class="col-md" id="use">
					<h3 id="titles"><fmt:message key="title.welcome"/></h3>
					<!-- <p id="content">
						The aim of AGP is to allow scholars and the public to explore
						ancient handwritten wall-inscriptions and to understand them in
						context. We have designed AGP to be a <b>user-friendly
							resource</b>. We provide maps to help viewers understand where
						graffiti appeared in the ancient city and we offer our own
						translations and brief summaries of the graffiti. Try out the  --> 
						<!--  brings the user's screen to focus on the two main interactive maps -->
					<!--	<a
							href="#top">maps</a> above, -->
							<!-- Brings the user to the list of all graffiti -->
							<!--<a
							href="<%=request.getContextPath()%>/results">browse</a> around,
						or begin a -->
						
						<!-- Brings the users cursor to the search bar when clicked -->
					<!--	<button id="searchButton" onmouseover="change_color()"
							onmouseout="original_color()" onclick="make_search_active()">search.</button>
						 <p style="padding-top:30px;">We hope you enjoy exploring the
						Ancient Graffiti Project and learning more about the ancient
						world!
					</p> -->
					<p id="content">
						<fmt:message key="welcome.pt1"/>
						<b><fmt:message key="welcome.pt2"/></b>
						<fmt:message key="welcome.pt3"/>
						<!--  brings the user's screen to focus on the two main interactive maps -->
						<a
							href="#top"><fmt:message key="welcome.maps"/></a> ,
							<!-- Brings the user to the list of all graffiti -->
							<a
							href="<%=request.getContextPath()%>/results"><fmt:message key="welcome.browse"/></a> 
							<fmt:message key="welcome.pt4"/>
						
						<!-- Brings the users cursor to the search bar when clicked -->
						<button id="searchButton" onmouseover="change_color()"
							onmouseout="original_color()" onclick="make_search_active()"><fmt:message key="welcome.search"/>.</button>
						 <p style="padding-top:30px;"><fmt:message key="welcome.pt5"/>
					</p>
				</div>
				<div class="col-md" id="guide">
					<h3 id="titles"><fmt:message key="title.editions"/></h3>
					<p id="content">
					<!--  	The inscriptions presented here are our critical editions of the
						ancient texts, many of which offer updates to the <i>Corpus
							Inscriptionum Latinarum</i>. We provide information on how to cite
						our editions in each entry. We have compiled up-to-date
						bibliography, a critical apparatus, and links to further
						information, and we include photographs from our fieldwork as well
						as the enhanced photographs and line-drawings we have created in
						order to accurately represent the inscriptions and make them
						legible to modern viewers. <br> <br>We are pleased to
						contribute our editions to the <a
							href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?lang=en">
							Epigraphic Database Roma</a> and <a
							href="https://www.eagle-network.eu/">EAGLE</a>, the Europeana
						network of Ancient Greek and Latin Epigraphy. For linked open data
						and teaching materials, please see the Resources menu above.-->
						<fmt:message key="editions.pt1"/> <i> Corpus
							Inscriptionum Latinarum</i>. <fmt:message key="editions.pt2"/> <br> <br><fmt:message key="editions.pt3"/> <a
							href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?lang=en">
							Epigraphic Database Roma</a> <fmt:message key="editions.pt4"/> <a
							href="https://www.eagle-network.eu/">EAGLE</a>, <fmt:message key="editions.pt5"/>
					</p>
				</div>
			</div>
			<p id="benefiel">
				<a href="https://www.wlu.edu/directory/profile?ID=x3919">Rebecca
					R. Benefiel</a>, <a href="http://ancientgraffiti.org/about/teams"><fmt:message key="footer.director"/></a>
			</p>
		</div>
	</main>

	<!-- Footer specifically for the home page. Adds links to benefactors, social media, and copyrights -->
	<footer id="frontpagefooter">
		<p style="text-align: center;">
			<a href="http://www.neh.gov/"> <img class="footer-img"
				style="width: 150px"
				src="<%=request.getContextPath()%>/resources/images/neh.jpg"
				alt="NEH"></a> <a href="http://digitalhumanities.wlu.edu/"> <img
				class="footer-img" style="height: 50px; width: 150px"
				src="http://ancientgraffiti.org/about/wp-content/uploads/2016/07/dh_at_wandl-1.png"
				alt="W&L Digital Humanitites"></a> <a href="https://mellon.org/">
				<img class="footer-img" style="height: 50px; width: 150px"
				src="http://ancientgraffiti.org/about/wp-content/uploads/2015/06/mellon-e1467740285109-1.jpeg"
				alt="Mellon Foundation">
			</a> <a href="http://chs.harvard.edu/"> <img class="footer-img"
				style="height: 72px; width: 150px"
				src="http://ancientgraffiti.org/about/wp-content/uploads/2017/06/CHS.png"
				alt="CHS Harvard"></a>
		</p>
		<p id="socials">
			Visit us on: <a
				href="https://www.facebook.com/HerculaneumGraffitiProject/"><img
				class="footer-img"
				style="width: 32px; margin-right: 2px; margin-left: 2px"
				src="<%=request.getContextPath()%>/resources/images/f-ogo_RGB_HEX-58.png"
				alt="facebook"></a> <a href="https://twitter.com/HercGraffProj"><img
				class="footer-img"
				style="width: 32px; margin-left: 0px; margin-right: 0px;"
				src="<%=request.getContextPath()%>/resources/images/Twitter_Social_Icon_Square_Color.png"
				alt="twitter"></a> <a
				href="https://github.com/AncientGraffitiProject/"><img
				class="footer-img" style="width: 32px; margin-left: 2px"
				src="<%=request.getContextPath()%>/resources/images/GitHub-Mark-32px.png"
				alt="github"></a>
		</p>
	</footer>

	<%@include file="footer.jsp"%>
</body>
</html>
