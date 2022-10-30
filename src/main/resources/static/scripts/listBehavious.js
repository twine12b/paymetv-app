 //<!-- Resolve video name Script -->
  	function collect_stats(thisFile){
 		
 		//Sets the Media ID for stats collection 		
 		//document.forms["collect"].elements["mediaId"].value = thisFile;
 		console.log("Ok stats should collect now!!.  " +thisFile);
 		//submit stats to servlet
 		//document.forms["collect"].submit();  		 	
 	}


//<!-- play file with preceding commercial -->
	$(function(){				
		$('div#clips a').click(function(e){
			// stop default clock action
			e.preventDefault();
		
			// collect selected file name
			var sLink = $(this).attr('href');
		
		//TODO - select random commercial
		var advert = '4f322304.flv';
		
				// stop whats playing & update the player object with the new selected clip
				$f().stop();
				 $f().unload();
				  $f().load();
				   $f().getClip(1).update({autoPlay:true, url: sLink});
				
				$f(0).play();
			
			//console.log(sLink);

		 return false;
		});	 
	});
 	 
//<!-- Show more/less text Script -->
$(document).ready(function() {
	var showChar = 100;
	var moretext = "more";
	var lesstext = "less";
	var ellipsesText = "...";
	$('.more').each(function() {
		var content = $(this).html();

		if(content.length > showChar) {

			var c = content.substr(0, showChar);
			var h = content.substr(showChar-1, content.length - showChar);

			var html = c + '<span class="moreellipses">' + ellipsesText + '&nbsp;</span><span class="morecontent"><span>' + h + '</span>&nbsp;&nbsp;<a href="" class="morelink">' + moretext + '</a></span>';
			$(this).html(html);
		}

	});

	$(".morelink").click(function(){
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

	
	$(document).ready(function() {
        // Creating hoverscroll with fixed arrows
		$('#clipsIT').hoverscroll({
            fixedArrows: true
        });
        // Starting the movement automatically at loading
        // @param direction: right/bottom = 1, left/top = -1
        // @param speed: Speed of the animation (scrollPosition += direction * speed)
        var direction = 1,
            speed = 3;
        $("#clipsIT")[0].startMoving(direction, speed);
	});
	
	 