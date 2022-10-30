<%@ page language="java" isELIgnored="false" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<jsp:directive.include file="/WEB-INF/sitemesh-decorators/include.jsp"/>
<fmt:setBundle basename="bundles.media-resources"/>

<html>
<head>

	<!-- the CSS for Smooth Div Scroll -->
	<link rel="Stylesheet" type="text/css" href="<%out.print(basePath); %>scroller/css/smoothDivScroll.css" />
	

	<!-- CSS for my specific scrolling content -->
	<style type="text/css">
	
		#makeMeScrollable
		{
			width:220%;
			height:120px;
			position:relative;
			
			margin-top:72%;
		}
	
		#makeMeScrollable div.scrollableArea *
		{
			position: relative;
			float: left;
			margin: 0;
			padding: 0;
			/* If you don't want the images in the scroller to be selectable, try the following
			   block of code. It's just a nice feature that prevent the images from
			   accidentally becoming selected/inverted when the user interacts with the scroller. */
			-webkit-user-select: none;
			-khtml-user-select: none;
			-moz-user-select: none;
			-o-user-select: none;
			user-select: none;
		}
	
	</style>
	

</head>
<body>

<div id="wrapper">
<div id="mainColumn">
	
	
	<!-- You should specify the width and height of the images. Otherwise Google Chrome
	can't calculate the total width of the scrollable area -->
	<div id="makeMeScrollable">
		<img src="<%out.print(basePath); %>scroller/images/demo/field.jpg" width="110px" height="100%" alt="Demo image" id="field" />
		<img src="<%out.print(basePath); %>scroller/images/demo/gnome.jpg" width="110px" height="100%" alt="Demo image" id="gnome" />
		<img src="<%out.print(basePath); %>scroller/images/demo/pencils.jpg" width="110px" height="100%" alt="Demo image" id="pencils" />
		<img src="<%out.print(basePath); %>scroller/images/demo/golf.jpg" width="110px" height="100%" alt="Demo image" id="golf" />
		<img src="<%out.print(basePath); %>scroller/images/demo/river.jpg" width="110px" height="100%" alt="Demo image" id="river" />
		<img src="<%out.print(basePath); %>scroller/images/demo/train.jpg" width="110px" height="100%" alt="Demo image" id="train" />
		<img src="<%out.print(basePath); %>scroller/images/demo/leaf.jpg" width="110px" height="100%" alt="Demo image" id="leaf" />
		<img src="<%out.print(basePath); %>scroller/images/demo/dog.jpg" width="110px" height="100%" alt="Demo image" id="dog" />

		<c:forEach items="${medias}" var="current" varStatus="i">
			<a href="${current.filename}.${current.fileType}">
				<img src="<%out.print(streamServer1); %>/thumbnails/${current.filename}.png" width="50" height="75" alt="${current.description}" onClick="collect_stats('${current.idmedia}')"/>
			</a>
		</c:forEach>


	</div>
		
</div>

	</div>
	<br/>
		<br/>
	
	<!-- LOAD JAVASCRIPT LATE - JUST BEFORE THE BODY TAG 
		 That way the browser will have loaded the images
		 and will know the width of the images. No need to
		 specify the width in the CSS or inline. -->
		 
		 <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>

	<!-- jQuery library - Please load it from Google API's [TODO - refactor]-->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js" type="text/javascript"></script>

	<!-- jQuery UI Widget and Effects Core (custom download)
		 You can make your own at: http://jqueryui.com/download -->
	<script src="<%out.print(basePath); %>scroller/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
	
	<!-- Latest version of jQuery Mouse Wheel by Brandon Aaron
		 You will find it here: http://brandonaaron.net/code/mousewheel/demos -->
	<script src="<%out.print(basePath); %>scroller/js/jquery.mousewheel.min.js" type="text/javascript"></script>

	<!-- Smooth Div Scroll 1.2 minified-->
	<script src="<%out.print(basePath); %>scroller/js/jquery.smoothdivscroll-1.2-min.js" type="text/javascript"></script>

	<!-- If you want to look at the uncompressed version you find it at
		 js/jquery.smoothDivScroll-1.2.js -->


	<!-- Plugin initialization -->
	<script type="text/javascript">
		// Initialize the plugin with no custom options
		$(document).ready(function () {
			// I just set some of the options
			$("#makeMeScrollable").smoothDivScroll({
				mousewheelScrolling: true,
				manualContinuousScrolling: true,
				visibleHotSpotBackgrounds: "always",
				autoScrollingMode: "onstart"
			});
		});
	</script>


</body>
</html>
