dojo.require("dijit.ProgressBar");

function doProgress() {
	//alert('Hello there!!s');
console.log("hey this has run!!");
	var button = window.document.getElementById("finish");
	var progress = window.document.getElementById("downloadProgress");
	progress.style.visibility="visible"; 
	button.disabled = true;
	var max = 100;
	var prog = 0;
	var counter = 0;
	//getProgress();
	//doProgressLoop(prog, max, counter);
}

function doProgressLoop(prog, max, counter) { 
   	var x = dojo.byId('progress-content').innerHTML;
   	var y = parseInt(x);
   	if (!isNaN(y)) {
   		prog = y;
   	}
   	jsProgress.update({ maximum: max, progress: prog });
	counter = counter + 1;
	dojo.byId('counter').innerHTML = counter;
   	if (prog < 100) {
   		setTimeout("getProgress()", 500);
   		setTimeout("doProgressLoop(" + prog + "," + max + "," + counter + ")", 1000);
   	}
}

function getProgress() {
    dojo.xhrGet({url: 'progress', // http://localhost:8080/file-upload/progress
                 load: function (data) { dojo.byId('progress-content').innerHTML = data; },
                 error: function (data) { dojo.byId('progress-content').innerHTML = "Error retrieving progress"; }
                });
}
