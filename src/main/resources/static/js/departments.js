var show = document.getElementById("display");
var content = document.getElementById("department-content");
var dptCode;
var currentData;
show.onclick = function(){
	let departments = document.getElementById("departments");
	let option = departments.options[departments.selectedIndex].value;
	dptCode = String(option);
	let data;
	let response = fetch("/admin/departments/view/?departmentCode="+option).then(textData => textData.text()).then((dataStr) => {
		content.innerHTML = "";
		data = JSON.parse(dataStr);
		currentData=data;
		if(data["universalSchedule"]==true){
			for(var key in data){
				content.innerHTML = content.innerHTML + "<label for='"+key+"'>"+key+"</label><input class='field' type='text' id='"+key+"' value='"+data[key]+"'/></br>";
			}
		}else{
			content.innerHTML = content.innerHTML + "<label for='name'>name</label><input class='field' type='text' id='name' value='"+data["name"]+"'/></br>";
			content.innerHTML = content.innerHTML + "<label for='universalSchedule'>universalSchedule</label><input class='field' type='text' id='universalSchedule' value='"+data["universalSchedule"]+"' readonly/></br>";
			let levels = data["levels"];
			for(let i = 0; i < levels.length; i++){
				let level = levels[i][String(i)];
				content.innerHTML = content.innerHTML+"<h2>Level "+i+": "+"</h2></br";
				for(var key in level){
					content.innerHTML = content.innerHTML + "<label for='"+key+String(i)+"'>"+key+"</label><input class='field-"+String(i)+"' type='text' id='"+key+String(i)+"' value='"+level[key]+"'/></br>";
			
				}
			}
		}
	});
}

update.onclick = function(){
	if(currentData!=null){
		for(var key in currentData){
			
			if(key!="levels"){
				currentData[key] = document.getElementById(String(key)).value;
			}else{
				let levels = currentData["levels"];
				for(let i = 0; i < levels.length; i++){
					let level = levels[i][String(i)];
					for(var levelKey in level){
						level[levelKey] = document.getElementById(String(levelKey)+String(i)).value;
					}
					levels[i][String(i)] = level;
				}
				currentData["levels"] = levels;
			}
		}
		
		let data = JSON.stringify(currentData);
		let dataObject = {};
		dataObject['"'+dptCode+'"'] = data;
		let dataJSON = JSON.stringify(dataObject);
		let req = new XMLHttpRequest();
		req.open("POST", "/admin/departments/set", true);
		req.setRequestHeader("Content-Type", "application/json");
		req.send(dataJSON);
	}
}