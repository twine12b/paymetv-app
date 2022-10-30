// <!-- Show more/less text Script -->
$(document).ready(function() {
	
	var showChar = 15;
	var moretext = "more";
	var lesstext = "less";
	var ellipsesText = "...";
	$('.more').each(function() {
		var content = $(this).html(); 


		
		console.log("Content size!!.  " +content.length);
		if(content.length > showChar) {

			var c = content.substr(0, showChar);
console.log("show text!!.  " +c);
			
			var h = content.substr(showChar-1, content.length - showChar);

			var html = c + '<span class="moreellipses">' + ellipsesText + '&nbsp;</span><span class="morecontent"><span>' + h + '</span>&nbsp;&nbsp;<a href="" class="morelink">' + moretext + '</a></span>';
//			<!-- $(this).html(html); -->
			$(".lastviewed .comment more").append(html);  // Todo - div is thisList [comment more]
		}

	});

	$(".morelink").click(function(){
		console.log("moreling has run!!");
		if($(this).hasClass("less")) {
			$(this).removeClass("less");
			$(this).html(moretext);
		} else {
			$(this).addClass("less");
			$(this).html(lesstext);
		}
		$(this).parent().prev().toggle();
		$(this).prev().toggle();
		return false;
	});
  });