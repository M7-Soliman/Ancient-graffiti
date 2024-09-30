
<script>
		window.onscroll = function(){scrollFunction()};
		function scrollFunction() {
			if (( document.body.scrollTop > screen.height/2 ) || (document.documentElement.scrollTop > screen.height/2)) {
			    document.getElementById("scroll_top").style.display = "inline";
			} else {
				document.getElementById("scroll_top").style.display = "none";
			}
		}
	</script>
<a href="#top" id="scroll_top" class="btn btn-dark">Return To Top</a>