$( document ).ready(function() {
	
	$('#commercialSelect').change(function(){
		var url = "http://debian.local:3000/thumbnails/";
		var hostname = document.location.origin;
		
		var contextPath= hostname;
		
		console.log(contextPath);
		console.log($("img[name=commercialPic]").attr("src",$(this).val()));
		$("img[name=commercialPic]").attr("src",url+$(this).val()+".png");
		$("#commercialFilename").val($(this).val());

	});
	
/**
 * No of days selector - actions
 */
	$('#no_of_days').change(function(){
		var date = new Date();
		    days = parseInt($("#no_of_days").val());
		
		console.log(date);
		console.log(days);
		
		 if(!isNaN(date.getTime())){
	            date.setDate(date.getDate() + days);
	            
	          console.log(date);  

            $("#endDate").val(date.toInputFormat());
        } else {
            alert("Invalid Date");  
        }
		 
		 
		// end - start returns difference in milliseconds
		 var start = $.format.date(new Date($('#startdate').val()), 'mm-dd-yyyy');
//		 var start = $.format.date(new Date(document.getElementById('startdate').value), 'dd-mm-yyyy');
		 var end = new Date ($('#endDate').val());
		 var diff = new Date(end - start);
		 var days = date/1000/60/60/24;
		 
		 console.log('document grab :' + document.getElementById('startdate').value);
		 
		 console.log("start date  " +start);
		 console.log("difference :  "+diff);
	});
	
	//From: http://stackoverflow.com/questions/3066586/get-string-in-yyyymmdd-format-from-js-date-object
    Date.prototype.toInputFormat = function() {
       var yyyy = this.getFullYear().toString();
       var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
       var dd  = this.getDate().toString();
       return (dd[1]?dd:"0"+dd[0]) + "/" + (mm[1]?mm:"0"+mm[0]) + "/" +  yyyy;  // padding
    };
	
});