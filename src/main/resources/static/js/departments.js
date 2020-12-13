var content = document.getElementById("department-content");
var dptCode;
var currentData;
display.onclick = function () {
	addLevel.style.display = "none";
	create.style.display = "none";
	add.style.display = "inline-block";
	update.style.display = "inline-block";
	content.style.display = "inline-block";
	let departments = document.getElementById("departments");
	let option = departments.options[departments.selectedIndex].value;
	dptCode = String(option);
	let data;
	let response = fetch("/admin/departments/view/?departmentCode=" + option).then(textData => textData.text()).then((dataStr) => {
		content.innerHTML = "";
		data = JSON.parse(dataStr);
		currentData = data;
		if (data["Universal schedule"] == "true") {
			for (var key in data) {
				content.innerHTML += "<label for='" + key + "'>" + key + "</label><input class='field' type='text' id='" + key + "' value='" + data[key] + "'/></br>";
			}
		} else {
			content.innerHTML += "<label for='Name'>Name: </label><input class='field' type='text' id='Name' value='" + data["Name"] + "'/></br>";
			content.innerHTML += "<label for='universalSchedule'>Universal schedule: </label><input class='field' type='text' id='Universal schedule' value='" + data["Universal schedule"] + "' readonly/></br>";
			let levels = data["levels"];
			for (let i = 0; i < levels.length; i++) {
				let level = levels[i][String(i)];
				content.innerHTML += "<h2><b>Level " + i + "</b></h2></br>";
				for (var key in level) {
					content.innerHTML += "<label for='" + key + String(i) + "'>" + key + "</label><input class='field-" + String(i) + "' type='text' id='" + key + String(i) + "' value='" + level[key] + "'/></br>";

				}
			}
		}
	});
}

update.onclick = function () {
	if (currentData != null) {
		for (var key in currentData) {
			if (key != "levels") {
				currentData[key] = document.getElementById(String(key)).value;
			} else {
				let levels = currentData["levels"];
				for (let i = 0; i < levels.length; i++) {
					let level = levels[i][String(i)];
					for (var levelKey in level) {
						level[levelKey] = document.getElementById(String(levelKey) + String(i)).value;
					}
					levels[i][String(i)] = level;
				}
				currentData["levels"] = levels;
			}
		}

		let data = JSON.stringify(currentData);
		let dataObject = {};
		dataObject['"' + dptCode + '"'] = data;
		let dataJSON = JSON.stringify(dataObject);
		let req = new XMLHttpRequest();
		req.open("POST", "/admin/departments/set", true);
		req.setRequestHeader("Content-Type", "application/json");
		req.send(dataJSON);
	}
}

var levelCounter = 0;
add.onclick = function () {
	update.style.display = "none";
	add.style.display = "none";
	content.style.display = "inline-block";
	let multilevel = confirm("Do you want to create a multi-level department");
	content.innerHTML = "";
	if (multilevel) {
		addLevel.style.display = "inline-block";
		content.innerHTML += "<label for='Name'>Name: </label><input class='data-field' type='text' id='Name'/></br>"
		+ "<label for='Universal schedule'>Universal schedule: </label><input class='data-field' type='text' id='Universal schedule' value='false' readonly/></br>"
		+ "<h2><b>Level " + levelCounter + "</b></h2></br><label for='Daily break duration total"+levelCounter+"'>Daily break duration total: </label><input class='data-field' type='text' id='Daily break duration total"+levelCounter+"'/></br>"
		+ "<label for='Schedule type"+levelCounter+"'>Schedule type: </label><input class='data-field' type='text' id='Schedule type"+levelCounter+"'/></br>"
		+ "<label for='Daily hours"+levelCounter+"'>Daily hours:</label><input class='data-field' type='text' id='Daily hours"+levelCounter+"'/></br>"
		+ "<label for='Shift length"+levelCounter+"'>Shift length: </label><input class='data-field' type='text' id='Shift length"+levelCounter+"'/></br>"
		+ "<label for='Work on weekends"+levelCounter+"'>Work on weekends: </label><input class='data-field' type='text' id='Work on weekends"+levelCounter+"'/></br>"
		+ "<label for='Monhtly work days"+levelCounter+"'>Monthly work days: </label><input class='data-field' type='text' id='Monhtly work days"+levelCounter+"'/></br>"
		+ "<label for='Employees per shift"+levelCounter+"'>Employees per shift: </label><input class='data-field' type='text' id='Employees per shift"+levelCounter+"'/></br>"
		+ "<label for='Break between shifts"+levelCounter+"'>Break between shifts: </label><input class='data-field' type='text' id='Break between shifts"+levelCounter+"'/>";

	} else {
		create.style.display = "inline-block";
		content.innerHTML += "<label for='Name'>Name: </label><input class='data-field' type='text' id='Name'/></br>";
		content.innerHTML += "<label for='Universal schedule'>Universal schedule: </label><input class='data-field' type='text' id='Universal schedule' value='true' readonly/></br>";
		content.innerHTML += "<label for='Daily break duration total'>Daily break duration total: </label><input class='data-field' type='text' id='Daily break duration total'/></br>";
		content.innerHTML += "<label for='Schedule type'>Schedule type: </label><input class='data-field' type='text' id='Schedule type'/></br>";
		content.innerHTML += "<label for='Daily hours'>Daily hours:</label><input class='data-field' type='text' id='Daily hours'/></br>";
		content.innerHTML += "<label for='Shift length'>Shift length: </label><input class='data-field' type='text' id='Shift length'/></br>";
		content.innerHTML += "<label for='Work on weekends'>Work on weekends: </label><input class='data-field' type='text' id='Work on weekends'/></br>";
		content.innerHTML += "<label for='Monhtly work days'>Monthly work days: </label><input class='data-field' type='text' id='Monhtly work days'/></br>";
		content.innerHTML += "<label for='Employees per shift'>Employees per shift: </label><input class='data-field' type='text' id='Employees per shift'/></br>";
		content.innerHTML += "<label for='Break between shifts'>Break between shifts: </label><input class='data-field' type='text' id='Break between shifts'/></br>";
	}
}

addLevel.onclick = function () {
	levelCounter++;
	create.style.display = "inline-block";
	let newFields = "<h2><b>Level " + levelCounter + "</b></h2></br><label for='Daily break duration total"+levelCounter+"'>Daily break duration total: </label><input class='data-field' type='text' id='Daily break duration total"+levelCounter+"'/></br>"
		+ "<label for='Schedule type"+levelCounter+"'>Schedule type: </label><input class='data-field' type='text' id='Schedule type"+levelCounter+"'/></br>"
		+ "<label for='Daily hours"+levelCounter+"'>Daily hours:</label><input class='data-field' type='text' id='Daily hours"+levelCounter+"'/></br>"
		+ "<label for='Shift length"+levelCounter+"'>Shift length: </label><input class='data-field' type='text' id='Shift length"+levelCounter+"'/></br>"
		+ "<label for='Work on weekends"+levelCounter+"'>Work on weekends: </label><input class='data-field' type='text' id='Work on weekends"+levelCounter+"'/></br>"
		+ "<label for='Monhtly work days"+levelCounter+"'>Monthly work days: </label><input class='data-field' type='text' id='Monhtly work days"+levelCounter+"'/></br>"
		+ "<label for='Employees per shift"+levelCounter+"'>Employees per shift: </label><input class='data-field' type='text' id='Employees per shift"+levelCounter+"'/></br>"
		+ "<label for='Break between shifts"+levelCounter+"'>Break between shifts: </label><input class='data-field' type='text' id='Break between shifts"+levelCounter+"'/>";
	let last = document.getElementById("Break between shifts"+String(levelCounter-1));
	last.insertAdjacentHTML('afterEnd' , newFields);
}

create.onclick = function () {
	let dataObject = {};
	let fields = document.getElementsByClassName("data-field");
	let universal = document.getElementById("Universal schedule");
	if(universal == true){
		for (let i = 0; i < fields.length; i++) {
			let field = fields[i];
			if (field.value != "") {
				dataObject[field.id] = field.value;
			}
		}
	}else{
		let fieldsArray = Array.from(fields);
		let fieldsSliced = fieldsArray.slice(2, fieldsArray.length); 
		dataObject["Name"] = document.getElementById("Name").value;
		dataObject["Universal schedule"] = document.getElementById("Universal schedule").value;
		let levels = [];
		for(let i = 0; i <= levelCounter; i++){
			
			let levelObj = {};
			let levelProperties = {};
			for(let j = 0; j < fieldsSliced.length; j++){
				if(fields[j+2].id.includes(String(i))){
					if(fields[j+2].value != ""){
						levelProperties[fields[j+2].id.replace(/[0-9]/g, '')] = fields[j+2].value;
					}
				}
			}
			if(JSON.stringify(levelProperties) !=='{}'){
				levelObj[String(i)] = levelProperties;
				levels[i] = levelObj;
				
			}
			
		}
		levelCounter = 0;
		dataObject["levels"]=levels;
	}
	
	currentData = dataObject;
	update.click();
}



