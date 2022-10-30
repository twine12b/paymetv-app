$( document ).ready(function() {
	/*$('[data-toggle="tooltip"]').tooltip(); 
	commercialEndFn();*/
	
	// Close button tasks
	$('a.close').on("click", function(e){
		stopAllVideos();
	});
	
	//right button Tasks
	$('a.right').on("click", function(e){
		stopAllVideos();
	});
	
	//left button Tasks
		$('a.left').on("click", function(e){
			stopAllVideos();
		});
	//carosel indicator/selectors
	$('.carousel-indicators li').on("click", function(e){
		stopAllVideos();
	});
  //Stop video playback when closing viewer
	// Refactor hard coded url
  function stopAllVideos(){
	  var id = $('video').attr('id');  //initialise [id] variable
	  var url = "https://192.168.44.155:3443/movies/";
	  var src = "";
		$('video').each(function() {
			id = $(this).attr('id');
			src = $(this).attr('name')+'.mp4';
			// console.log('***** stopallfn:  ' +id);
			$(this).get(0).pause();
		    $(this).currentTime = 0;
		    $(this).attr('src', url+src); // set src to set the poster
		      $(this).load();
		    togglePlayButton(id);
		});	
  }
  
  function togglePlayButton(vidPlayer){
		var id = $.trim(vidPlayer.replace('video_', ''));
		//console.log('***** id :  ' +id);
		$('#'+id).addClass("video-button");  
			$('#'+vidPlayer).prop("controls", false);
					//add event back onto button div
					$('div#'+id).on("click", function(){
						statsandPlayFn(id);
					}); 
			
			//console.log('how U doing: ' +id);
			$('div#'+id).addClass("video-button");
  }
  
  //Show synopsis from mobrise tiles
	$('.mbr-gallery-title').hover(function(){
	  
  	var thisId = $(this).closest('div').attr('id');
  	var fullDivId = "div_"+thisId;
  	var parentImgHeight = $(this).closest('div').find('img').height();
  	
  	  $('.mbr.gallery.row').height(parentImgHeight+'px');

  	$(this).closest('div').find('div#'+fullDivId).height(parentImgHeight-4+'px');
  	$(this).closest('div').find('div#'+fullDivId).toggleClass("showIt");
  });
  
  	// 2nd tool function bar
	$( "label#vidInfo" ).on("click", function(e){
		alert('info pressed');
	});
	$( "label#like" ).on("click", function(e){
		//TODO - only show during commercial playback
		alert('like pressed');
	});
	$( "label#dislike" ).on("click", function(e){
		//TODO - only show during commercial playback
		alert('dislike pressed');
	});
	$( "label#reportVid" ).on("click", function(e){
		//TODO - only show during selected film play back
		var vId = $(this).closest(".carousel-item").find('video').attr('id');
		//var vidSrc = $('#' + vId);
		alert('Report video pressed:  ' +vId);
		//alert('Report video pressed:  ' +vidSrc);
	});
	
	//TODO - fix syntax on this listener
	function commercialEndFn(){
		$('video').each(function() {
			$(this).on('ended',function(){
			    console.log('Video has ended!');
			    $(this).attr('controls', '');
			});
		});
	}
	
	//capture old functionality [stats,hitcounter etc] - needs to be on play button
	$('video').each(function() {
		var vidId = $(this).attr('id');
		$(this).on('play',function(){
		    console.log('Video is playing...!  ' +vidId);
		    statsandPlayFn(vidId);
		});
	});
	
	//Disable mobrise enlarged navigation buttons
	function disableMobriseNav(){
		//$('a#leftCarousel-Control').prop('disabled', true);
		//$('a#rightCarousel-Control').prop('disabled', true);
		//$('a#carousel-Close').prop('disabled', true);
		
		$('#leftCarousel-Control').hide();
		$('#rightCarousel-Control').hide();
		$('#carousel-Close').hide();		
		$('.carousel-indicators').hide();
	}
	
	//Enable mobrise enlarged navigation buttons
	function enableMobriseNav(){
		//$('a#leftCarousel-Control').prop('disabled', false);
		//$('a#rightCarousel-Control').prop('disabled', false);
		//$('a#carousel-Close').prop('disabled', false);

		$('#leftCarousel-Control').show();
		$('#rightCarousel-Control').show();
		$('#carousel-Close').show();
		$('.carousel-indicators').show();
	}
	
		$('div.video-button').click(function() {
			//get the current video - travers the DOM
			var id = $(this).attr('id');
			//toggle off left right and exit
			disableMobriseNav();
				//pass details over to the helper script
				statsandPlayFn(id);
		});
	
	//capture old functionality [stats,hitcounte etc] - needs to be on play button
//	$( "div.myClass, img" ).on("click", function(e){
//		var id = $(this).attr('id');
//		e.preventDefault();
//		//e.stopPropagation();
//		alert('Image pressed ' +id);
//	});
  	

//<!-- play file with preceding commercial -->
function statsandPlayFn(id){
	//Get only the video ID
	console.log('working  ' +id);
//	var id = $.trim(vidId.replace('video_', ''));
//	var fname = ''+id;
	var player = $('#video_'+id);	
	//	if (player.hasAttribute("controls")) {
	//	     player.removeAttribute("controls")   
	//	  } 
		
	
	
		// Select random commercial
        $.get("PMTVCommercialSelector", 
        {
            fileId : id
        },
        function(textreceived){
        	var advertFileName = textreceived[0]; //filename
        	var moviefilename = textreceived[3];  //user selected filename
        	var url = textreceived[4];
        	var commercialHasBeenPlayed = false;
      	  	
        	//var commercial = "https://192.168.44.155:3443/movies/"+advertFileName+".mp4";
   	    	//var movie = "https://192.168.44.155:3443/movies/" +moviefilename+ ".mp4";
        	var commercial = url + advertFileName+ ".mp4";
   	    	var movie = url +moviefilename+ ".mp4";
        	
      	  		// disableMobriseNav();
        	
      	  		//remove playbutton
      	  		$('#'+id).removeClass("video-button");
      	  		 $('#'+id).unbind('click');
      	  		
      	  		
        	$(player).attr('src', commercial);
        	$(player).get(0).play();
        	commercialHasBeenPlayed = true; //stops repeated plays
        	
        	$(player).on('ended',function(){
			    console.log('Inner Video has ended!');
			    $(player).get(0).pause();
			     $(player).attr('controls', '');
			  if(commercialHasBeenPlayed == true) {
				  commercialHasBeenPlayed = false;
			      $(player).attr('src', movie);
			     	$(player).get(0).play();
			     	enableMobriseNav();  //enable navigation after commercial playback 
        	  }
			});
        	
         	
//      	  	console.log('arr0  ' + advertFileName);
        });
}

//TODO - video reset
//add class to div
//add event to div
//rewind vid ???

});




































           
              
	// stop whats playing & update the player object with the new selected clip
//	$f().stop();
//	 $f().unload();
//	  $f().load();
//	  $f().getClip(0).update({autoPlay:true, url: advert});  // update advert element
//	   $f().getClip(1).update({autoPlay:true, url: sLink});  // update video element
	
//	$f(0).play();
	
//	update_lastViewed(id);
	
	// Set Stats commercial id
//	$('#commercial_filename').val( advertFileName );
	
	// Set contract id
//	$('#stats_contract').val( "" );  // clear contents
//	$('#stats_contract').val( advertId ); // fix				
		
	// Get royalty / advRate / endUserId	
//		----getRateRoyalUserStat( advertId );	
		
	//<!-- collect statistics -->				
//	doHitCounter(id, textreceived);
//	 doStatsButtonPress();  // collect this first

//});	 
