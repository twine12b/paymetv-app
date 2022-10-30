/**
 * These scripts add additional functionality to the navigation buttons in this
 * project.  Some functions are basic such as [visual indicator that a button has been clicked].
 * While others will be used to run spring classes [such as collect Statistics] 
 * 
 * P.S. Buttons on a page will need to be named /Id correctly.
 */
// next button
 $(document).ready(function() {
    $("#next").click(function(){
    	$(this).text('processing...');  //TODO - add I18N
		//$("#next").attr("disabled", true);
    }); 
    
 //Back button   
    $("#back").click(function(){
    	$(this).text('processing...');  //TODO - add I18N
		//$('input:submit').attr("disabled", true);
    });  
    
 //Finnish button   
    $("#finish").click(function(){
    	var hasFile = $('#filedata').val();
    	console.log("filedata  " +hasFile);
		
    	if(hasFile == null || hasFile == ""){
    		//Do Nothing
    	} else{
    		//$("#finish").attr("disabled", true);
    		
        	$(this).text('Processing...');  //TODO - add I18N
    		$('#animation').attr("hidden", false);
    	}
    });     
});