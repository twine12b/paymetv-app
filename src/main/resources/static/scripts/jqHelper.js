$( document ).ready(function() { //attempt to fix random data loss
	
	function update_lastViewed(id){
  		$(function(){			
  			console.log("The id number selected is :  " +id);
  			
  			$.ajax({
  				type: "GET",
  				url: 'fifoLastViewed',
  				//dataType : 'html/text', // expected returned data format.
  				data: {id: id},
  				success: function(response) {
  					//added [IF] statement as fix of dynamic update on div
  					 if (response.status != "success") { 
	  						$('#lastviewed').load("userViewedList");

  							$('body').on('click','#lastviewed b a',function(e){
  								//alert('prevent link');
  								// stop default click action
  								e.preventDefault();
  							
  								// collect selected file name
  								var sLink = $(this).attr('href');
  								var fname = $(this).attr('alt');
  								var fInfo = $(this).attr('name');
  								var id = $(this).attr('id');
  								
  								$("div#fileName p").html(fname);
  								$("div#fileInfo p").html(fInfo);
  								
  							//TODO - select random commercial
  							var advert = '4f2a7bd2.flv';
  							
  									// stop whats playing & update the player object with the new selected clip
  									$f().stop();
  									 $f().unload();
  									  $f().load();
  									   $f().getClip(1).update({autoPlay:true, url: sLink});
  									
  									$f(0).play();
  									
  									update_lastViewed(id);
  								
  								console.log(sLink);
  								
//  							      doHitCounter(id, sLink);
  							    doHitCounter(id);
  								appendStatsMediaId(id)
  									
  							 return false;
  							});
  					 }
  				}
			});
  		});
  	}
	
	//<!-- play file with preceding commercial -->
	$(function(){				
		$('div#makeMeScrollable img').on("click", function(e){

			console.log("This is the advert:  " +advert);
			
			e.preventDefault();
			e.stopPropagation();
					
			// collect selected file name
//			var sLink = $(this).attr('href');
			var fname = $(this).attr('alt');
			var fInfo = $(this).attr('name');
			var id = $(this).attr('id');	
			var sLink = $(this).attr('name');  // re-factored attribute 
			
//			alert('I get  :   ' + fname); 
			$("div#fileName p").html(fname);
			$("div#fileInfo p").html(fInfo);
			
					
			// Select random commercial
		            $.get("PMTVCommercialSelector", 
		            {
		                fileId : id
		            },
		            function(textreceived){
//		                  alert('In Callback. Text Received is: '+textreceived);
		            	  var advertFileName = textreceived[0]; //filename
		            	  var advertId = textreceived[1]; //fileId
		                  advert = textreceived[0]+'.flv';  
//		                  alert(advertFileName+", " +advertId);
		                  
				// stop whats playing & update the player object with the new selected clip
				$f().stop();
				 $f().unload();
				  $f().load();
				  $f().getClip(0).update({autoPlay:true, url: advert});  // update advert element
				   $f().getClip(1).update({autoPlay:true, url: sLink});  // update video element
				
				$f(0).play();
				
				update_lastViewed(id);
				
				// Set Stats commercial id
				$('#commercial_filename').val( advertFileName );
				
				// Set contract id
				$('#stats_contract').val( "" );  // clear contents
				$('#stats_contract').val( advertId ); // fix				
					
				// Get royalty / advRate / endUserId	
//					----getRateRoyalUserStat( advertId );	
					
				//<!-- collect statistics -->				
				doHitCounter(id, textreceived);
				 doStatsButtonPress();  // collect this first

		});	 
	 });
	});
	
	function getRateRoyalUserStat(advertId){
		
		$("div#stats_area").load("getRateRoyalUserStat");	
//		alert(advertId);
//		$('#stats_Adv').val( "" );  // clear contents
//		$('#stats_royalty').val( "" );  // clear contents
//		
//		// get extra stat info
//	    $.get("getRateRoyalUserStat", function(textreceived){
//        	  var endUser = textreceived[0];
//        	  var advRate = textreceived[1];
//        	  var royalty = textrecieved[2];
//        	  
//        	  alert(endUser +", " +advRate +", " +royalty);
//
//    	  $('#stats_endUserSystemUserIduser').val(textreceived[0]);
//    	  $('#stats_Adv').val(textreceived[1]);
//    	  $('#stats_royalty').val(textrecieved[2]);
//
//	     });	 
//		

	}
	
	/**
	 * Function to append the mediaId number on the stats table
	 */
	function doStatsButtonPress(){	
		$('#stats_save_btn').trigger('click');
	}
	
	/**
	 * Function calls a void controller to perform a increment of the media 
	 * hitCounter field.
	 * [fix] - null value being sent. this line fixes the issue by populating
	 * the fields name value. 
	 */
	function doHitCounter(id, textreceived){
		//refresh stats pane before collecting stats
//		$('#stats_area').load("https://localhost:8443/paymetv/collectStats"); //not working
		
		$('#stats_mediaMediaId1').val( id );
		$("input[name=mediaMediaId1]").val(id); // fix
		$('div#movieTitle p').text(textreceived[2]);  //show chosen Movie title	
		
		// Define url location
		var url = location.protocol + "//" + location.hostname 
			+ ':' + location.port + "/paymetv/doHitCounter?mediaId=" +id;
		
		//alert(url);
		
		$.ajax({
			  type: "POST",
			  url: url,
			  success: function(response) {
				  console.log("The media [ " +id +" ] has been incremented");
			  }
		});
	}
	
 	 
 //<!-- Show more/less text Script -->
$(document).ready(function() {
	
	/**
	 * Prevent default action of form submit after
	 * programmatic press of submit button 
	 * NB: [Bug] wont update 1st instance of mediaMediaId1
	 * FIX - send form twice - wont save if mediaId is null
	 */
	$( "#f" ).submit(function(event) {

		event.preventDefault();	
	    $this = $(this);  // MUST specify this variable
//	    alert($this.serialize());
	    
	    $.ajax({
	       type: "POST",
	       url: $this.attr('action'),
	       data: $this.serialize(), 
	       success : function(){
//	          alert('Done');
	       }  
	    });
	    return false;
	});
	
	//*******************************************************
	$("#clickMe").click(function(e) {
	    e.stopPropagation();
	    //alert('clickMe');
	});
	//*******************************************************
		
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

	//<!--grow shrink information box onMouseOver -->
		$(function(){
			$('#info').stop().hover(function(){
				//alert('so far so good');

				  $('#fileInfo').stop().fadeIn(1200);
				console.log("mouse on");
			}, function(){
				$('#fileInfo').stop().fadeOut(1200);
			});
		});
	
		function doBusiness(){
			
		}
		
//<!-- play file with preceding commercial from lastviwed panel -->
	$(function(){				
		$('div#lastviewed b a').click(function(e){	
			
			//alert('prevent link');
			// stop default click action
			e.preventDefault();
		
			// collect selected file name
			var sLink = $(this).attr('href');
			var fname = $(this).attr('alt');
			var fInfo = $(this).attr('name');
			var id = $(this).attr('id');
			
			$("div#fileName p").html(fname);
			$("div#fileInfo p").html(fInfo);
			
					
			// Select random commercial
            $.get("PMTVCommercialSelector", 
            {
                fileId : id
            },
            function(textreceived){
                  var advert = textreceived+'.flv';
		
				// stop whats playing & update the player object with the new selected clip
				$f().stop();
				 $f().unload();
				  $f().load();
				   $f().getClip(1).update({autoPlay:true, url: sLink});
				
				$f(0).play();
				
				update_lastViewed(id);
			
//			doHitCounter(id, textreceived);
			doHitCounter(id);
			appendStatsMediaId(id)

		 return false;
		});	 
 	  });
	});		
	
}); //end