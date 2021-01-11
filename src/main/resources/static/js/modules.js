var updatedActives = {};
var moduleNamesElements = document.getElementsByClassName("module-name");
var moduleNames = [];
for(let i=0;i< moduleNamesElements.length; i++){
	moduleNames[i] = moduleNamesElements[i].innerHTML;
}

$(".module-update").click(function(){
	for(let i = 0; i < moduleNames.length; i++){
		let name = moduleNames[i];
		updatedActives[moduleNames[i]] = document.getElementById(name).checked;
	}
	let jsonProto = {};
	jsonProto.updatedActives = JSON.stringify(updatedActives);
	let json = JSON.stringify(jsonProto);
	let  req = new XMLHttpRequest();
	req.open("POST", "/admin/modules/set/", true);
	req.setRequestHeader("Content-Type", "application/json");
	req.send(json);
	window.location
});