function startTime()
{
	var today=new Date();
	var h=today.getHours();
	var m=today.getMinutes();
	var s=today.getSeconds();
	var d=today.getDate();
	var m=today.getMonth();
	var y=today.getYear()+1900;
		
	//fix day length
	d=fixDigit(d);
	
	//fix month length
	//m=fixDigit(m);
		
	// add a zero in front of numbers<10
		m=checkTime(m);
		s=checkTime(s);
		document.getElementById('dateTime').innerHTML=d+"/" +m+"/"+y+" - " + h+":"+m+":"+s;
		t=setTimeout('startTime()',500);
		
	// sub routine to set file value from Debian php
		setFileName();
}

	function checkTime(i)
	{
		if (i<10)
  	{
  		i="0" + i;
  	}
		return i;
	}
	
	function fixDigit(x){
		var digit = x.toString();
		
		if (digit.length < 2){
			digit = '0' + digit;  //add a leading zero
		}
		return digit;
	}
	
	// sets the file name with the generated hex name
	//TODO trim leading white space
	//todo - trim trailing white space
	function setFileName(){
		
		var f = hexFileName();  //get php hex file name
		  f = trim(f);  // trims excess white space
		  
		   document.getElementById('file_name').value = f;		
	}
	
	
	function trim(strText) { 
    // this will get rid of leading spaces 
    while (strText.substring(0,1) == ' ') 
        strText = strText.substring(1, strText.length);

    // this will get rid of trailing spaces 
    while (strText.substring(strText.length-1,strText.length) == ' ')
        strText = strText.substring(0, strText.length-1);

   return strText;
}