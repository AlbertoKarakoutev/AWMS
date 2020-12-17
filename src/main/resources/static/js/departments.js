var content = document.getElementById("department-content");
var dptCode;
var currentData;
display.onclick = function () {
	addLevel.style.display = "none";
	create.style.display = "none";
	add.style.display = "inline-block";
	update.style.display = "inline-block";
	deleteBtn.style.display = "inline-block";
	content.style.display = "block";
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
				content.innerHTML += "<input class='data-field form-control' type='text' id='"+key+"' value='"+ data[key] +"'><small class='form-text text-muted'>"+key+"</small><br>";
			}
		} else {
			content.innerHTML += "<input class='data-field form-control' value='"+data["Name"]+"' type='text' id='Name'/><small class='form-text text-muted'>Employee's name.</small><br>";
			content.innerHTML += "<input class='data-field form-control' type='text' id='Universal schedule' value='" + data["Universal schedule"] + "' readonly/><small class='form-text text-muted'>Applies for the entire deaprtment</small><br>";
			
			let levels = data["levels"];
			for (let i = 0; i < levels.length; i++) {
				let level = levels[i][String(i)];
				content.innerHTML += "<h2><b>Level " + i + "</b></h2></br>";
				for (var key in level) {
					content.innerHTML += "<div class='form-group'><label for='" + key + String(i) + "'>" + key + "</label><input class='form-control field-" + String(i) + "' type='text' id='" + key + String(i) + "' value='" + level[key] + "'/></div>";

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

deleteBtn.onclick = function () {
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
		req.open("POST", "/admin/departments/delete", true);
		req.setRequestHeader("Content-Type", "application/json");
		req.send(dataJSON);
		window.location.assign("/admin/departments");
	}
}

var levelCounter = 0;
add.onclick = function () {
	update.style.display = "none";
	deleteBtn.style.display = "none";
	
	let multilevel = confirm("Do you want to create a multi-level department");
	if(content.innerHTML != ""){
		content.innerHTML = "";
	}
	if (multilevel) {
		addLevel.style.display = "inline-block";
		add.style.display = "none";
		content.innerHTML += "<input class='data-field form-control' placeholder='Name' type='text' id='Name' required><small class='form-text text-muted'>Department name</small><br>"
		+ "<input class='data-field form-control' type='text' id='Universal schedule' value='false' readonly/><small class='form-text text-muted'>Information applies for the entire deaprtment</small><br>"
		+ "<input class='data-field form-control' placeholder='Code' type='text' id='departmentCode'/><small class='form-text text-muted'>Optional: Manually enter the department code</small><br>"
		+ "<h2><b>Level " + levelCounter + "</b></h2><br><input class='data-field form-control' placeholder='Breaks' type='number' id='Daily break duration total"+levelCounter+"'/><small class='form-text text-muted'>Total break time for the day.</small><br>"
		+ "<select class='data-field form-control' id='Schedule type"+levelCounter+"'/><option value='Regular' selected>Regular</option><option value='Irregular'>Irregular</option><option value='OnCall'>On Call</option></select>"
		+ "<small class='form-text text-muted'>Type of scheduling for the department</small><br>"
		+ "<input class='data-field form-control' placeholder='Start-hour, Start-minutes, End-hour, End-minutes' type='text' id='Daily hours"+levelCounter+"'/><small class='form-text text-muted'>Department's open hours</small><br>"
		+ "<input class='data-field form-control' placeholder='Length of Shift' type='number' id='Shift length"+levelCounter+"'/><small class='form-text text-muted'>Each employee's shift length</small><br>"
		+ "<select class='data-field form-control' id='Work on weekends"+levelCounter+"'/><option value='true' selected>Yes</option><option value='false'>No</option></select>"
		+	"<small class='form-text text-muted'>Do the employees work on the weekends</small><br>"
		+ "<input class='data-field form-control' placeholder='Number of days' type='number' id='Monthly work days"+levelCounter+"'/><small class='form-text text-muted'>Work days per month</small><br>"
		+ "<input class='data-field form-control' placeholder='Number of employees' type='number' id='Employees per shift"+levelCounter+"'/><small class='form-text text-muted'>Number of employees for each shift</small><br>"
		+ "<input class='data-field form-control' placeholder='Hours' type='number' id='Break between shifts"+levelCounter+"'/><small class='form-text text-muted'>Minimum break time between an employee\'s shifts</small><br>";
		
		let refresh = content.innerHTML;
		content.innerHTML=refresh;
	} else {
		create.style.display = "inline-block";
		add.style.display = "none";
		content.innerHTML += "<input class='data-field form-control' placeholder='Name' type='text' id='Name' required><small class='form-text text-muted'>Department name.</small><br>";
		content.innerHTML += "<input class='data-field form-control' type='text' id='Universal schedule' value='true' readonly/><small class='form-text text-muted'>Information applies for the entire deaprtment</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Code' type='text' id='departmentCode'/><small class='form-text text-muted'>Optional: Manually enter the department code</small><br>"
		content.innerHTML += "<input class='data-field form-control' placeholder='Breaks' type='number' id='Daily break duration total'/><small class='form-text text-muted'>Total break time for the day.</small><br>";
		content.innerHTML += "<select class='data-field form-control' id='Schedule type'><option value='Regular' selected>Regular</option><option value='Irregular'>Irregular</option><option value='OnCall'>On Call</option></select>";
		content.innerHTML += "<small class='form-text text-muted'>Type of scheduling for the department</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Start-hour, Start-minutes, End-hour, End-minutes' type='text' id='Daily hours'/><small class='form-text text-muted'>Department's open hours</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Length of Shift' type='number' id='Shift length'/><small class='form-text text-muted'>Each employee's shift length</small><br>";
		content.innerHTML += "<select class='data-field form-control' id='Work on weekends'/><option value='true' selected>Yes</option><option value='false'>No</option></select>";
		content.innerHTML += "<small class='form-text text-muted'>Do the employees work on the weekends</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Number of days' type='number' id='Monthly work days'/><small class='form-text text-muted'>Work days per month</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Number of employees' type='number' id='Employees per shift'/><small class='form-text text-muted'>Number of employees for each shift</small><br>";
		content.innerHTML += "<input class='data-field form-control' placeholder='Hours' type='number' id='Break between shifts'/><small class='form-text text-muted'>Minimum break time between an employee\'s shifts</small><br>";
	}
}

addLevel.onclick = function () {
	levelCounter++;
	create.style.display = "inline-block";
	let newFields = "<h2><b>Level " + levelCounter + "</b></h2><br><input class='data-field form-control' placeholder='Breaks' type='number' id='Daily break duration total"+levelCounter+"'/><small class='form-text text-muted'>Total break time for the day.</small><br>"
		+ "<select class='data-field form-control' id='Schedule type"+levelCounter+"'/><option value='Regular' selected>Regular</option><option value='Irregular'>Irregular</option><option value='OnCall'>On Call</option></select>"
		+ "<small class='form-text text-muted'>Type of scheduling for the department</small><br>"
		+ "<input class='data-field form-control' placeholder='Start-hour, Start-minutes, End-hour, End-minutes' type='number' id='Daily hours"+levelCounter+"'/><small class='form-text text-muted'>Department's open hours</small><br>"
		+ "<input class='data-field form-control' placeholder='Length of Shift' type='number' id='Shift length"+levelCounter+"'/><small class='form-text text-muted'>Each employee's shift length</small><br>"
		+ "<select class='data-field form-control' id='Work on weekends"+levelCounter+"'/><option value='true' selected>Yes</option><option value='false'>No</option></select>"
		+	"<small class='form-text text-muted'>Do the employees work on the weekends</small><br>"
		+ "<input class='data-field form-control' placeholder='Number of days' type='number' id='Monthly work days"+levelCounter+"'/><small class='form-text text-muted'>Work days per month</small><br>"
		+ "<input class='data-field form-control' placeholder='Number of employees' type='number' id='Employees per shift"+levelCounter+"'/><small class='form-text text-muted'>Number of employees for each shift</small><br>"
		+ "<input class='data-field form-control' placeholder='Hours' type='number' id='Break between shifts"+levelCounter+"'/><small class='form-text text-muted'>Minimum break time between an employee\'s shifts</small><br>";
	
	let last = document.getElementById("Break between shifts"+String(levelCounter-1));
	last.insertAdjacentHTML('afterEnd' , newFields);
}

create.onclick = function () {
	let dataObject = {};
	let fields = document.getElementsByClassName("data-field");
	let universal = document.getElementById("Universal schedule").value;
	if(universal == "true"){
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
		console.log(levels);
	}
	
	currentData = dataObject;
	dptCode="undefined";
	update.click();
}



