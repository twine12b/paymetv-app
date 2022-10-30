
var geocoder;
initialize();
codeLatLng();
function initialize() {

    geocoder = new google.maps.Geocoder();
}

function codeLatLng() {
	
	var input = document.getElementById('stats_latitude').value + "," 
					+ document.getElementById('stats_longitude').value;
	
	console.log(input);
	var latlngStr = input.split(",", 2);
    var lat = parseFloat(latlngStr[0]);
    var lng = parseFloat(latlngStr[1]);
    var latlng = new google.maps.LatLng(lat, lng);
    geocoder.geocode({
        'latLng': latlng
    }, function(results, status) {
    	var st$ = '' + (results[1].formatted_address); + '';
        //document.getElementById("test").innerHTML = st$; //show retrieved GPS address
        document.getElementById("tst").value = st$;
        
        getPostCode(st$);
    });
}

/**
 * Get postcode also gets country code and updates stats fields with 
 * the appropriate values.  Ipaddress lookup is currently inacurate. so GPS
 * values are used to override Ipaddress lookup data when ever GPS values are found.
 */
function getPostCode(st$){
	console.log('value of st$ is:  ' + st$);
	var strLength = st$.length - 1;
	var newStr = '';
	console.log('length is:' +strLength)
	
	while ((strLength) >= 0 && (st$.charAt(strLength) != ',')){
				newStr += st$.charAt(strLength);
				console.log(newStr);
		strLength --;
	}
	newStr = reverse(newStr);
	document.getElementById('stats_countryCode').value = newStr;
	
	var numOfsp = 0;
	var pCode = '';
	strLength --;  // move to right of 'comma'
	while(strLength >=0 && numOfsp < 2){
		if(st$.charAt(strLength) == ' '){ numOfsp ++;}
		pCode += st$.charAt(strLength);
		strLength --;
	}
	document.getElementById('stats_postCode').value = reverse(pCode);
}


// reverse a string
function reverse(newStr){	
	console.log(newStr);
	var size = newStr.length;
	console.log ('newStr size is :  '+ size)
	var reverseString = '';
	
	while(size >= 0) {
		reverseString += newStr.charAt(size);
		size --;
	}
	console.log('reversed sting was ' +newStr + 'now ' + reverseString);
	return reverseString.trim();
}