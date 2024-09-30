<footer>
	<%@ page
		import="java.util.Calendar,java.util.GregorianCalendar,java.util.Date"%>
	<div class="container">
		<%
		Calendar calendar = new GregorianCalendar();
		Date date = new Date();
		calendar.setTime(date);
		int currentYear = calendar.get(Calendar.YEAR);
		%>
		<p id="copyright">
			This work is licensed under a <a id="license" rel="license"
				href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative
				Commons Attribution-NonCommercial-ShareAlike 4.0 International
				License</a> <br>&copy;2013-<%=currentYear%>
			- <a href="http://ancientgraffiti.org/about/terms-of-use">Terms
				of Use</a> <br> <a
				href="http://ancientgraffiti.org/about/main/versions/version-5-0-0/">Version
				5.0.0</a>
		</p>
	</div>
	<script src="https://code.jquery.com/jquery-3.6.4.min.js"
		integrity="sha256-oP6HI9z1XaZNBrJURtCoUT5SUnxFr8s3BzRl+cbzUq8="
		crossorigin="anonymous"></script>
	<!-- <script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
		crossorigin="anonymous"></script> -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/resources/js/pompeiiMap.js"></script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/resources/js/herculaneumMap.js"></script>
</footer>