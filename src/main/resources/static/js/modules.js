var updatedActives = {};
var moduleNamesElements = document.getElementsByClassName("module-name");
var moduleNames = [];
for(let i=0;i< moduleNamesElements.length; i++){
	moduleNames[i] = moduleNamesElements[i].innerHTML;
}

updateModules.onclick = function(){
	for(let i = 0; i < moduleNames.length; i++){
		let name = moduleNames[i];
		updatedActives[name] = document.getElementById(name).checked;
	}
	let jsonProto = {};
	jsonProto.updatedActives = JSON.stringify(updatedActives);
	let json = JSON.stringify(jsonProto);
	let  req = new XMLHttpRequest();
	req.open("POST", "/admin/modules/set/", true);
	req.setRequestHeader("Content-Type", "application/json");
	req.onreadystatechange = function(){
		if(req.status == 200 && req.readyState==4){
			alert("Modules updated!");
			window.location = "/admin/modules";
		}
	}
	req.send(json);
}