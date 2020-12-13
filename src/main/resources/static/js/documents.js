var downloadButtons = document.getElementsByClassName("download");

Array.from(downloadButtons).forEach(function(button){
    button.onclick = function(){
        let documentType = window.location.href.split("/")[4];
        let req = new XMLHttpRequest();
        req.open("GET", "/document/" + documentType + "/download/"+button.id, true);
        req.responseType = "blob";
        req.onload = function (event) {
            var blob = req.response;
            var fileName = req.getResponseHeader("fileName");
            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download=fileName;
            link.click();
        };
        req.send();

    }
});

search.onclick = function(){
	let key = searchTerm.value;
	if(key!=""){
		window.location.assign("/document/public/search/?name="+key);
	}else{
		window.location.assign("/document/public");
	}
}
