var currentData;
var content = document.getElementById("department-content");
var levelCounter = 0;

display.onclick = function () {

	let departments = document.getElementById("departments");
	let option = departments.options[departments.selectedIndex].value;

	addLevel.style.display = "none";
	create.style.display = "none";
	if(option !== undefined){
		update.style.display = "inline-block";
		deleteBtn.style.display = "inline-block";
	}
	add.style.display = "inline-block";
	
	levelCounter = 0;
	
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/departments/view/?departmentCode=" + option, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status==200){
			let resData = JSON.parse(req.response);
			currentData = resData;
			content.innerHTML = "";
			if (resData["universalSchedule"] == true) {
				insertForm(resData);
			} else {
				globalFields(resData, false);
				let levels = resData["levels"];
				for (let i = 0; i < levels.length; i++) {
					content.innerHTML += "<h2><b>Level " + i + "</b></h2><br>";
					insertLevelForm(resData, i);
				}
			}
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...")
		}
	}
	req.send();
}

update.onclick = function () {
	if(document.getElementById("name").value !== ""){
		if (currentData != null) {
			for (var key in currentData) {
				if (key != "levels") {
					if(document.getElementById(key)!=null){
						currentData[key] = document.getElementById(key).value;
					}
				} else {
					let levels = currentData["levels"];
					for (let i = 0; i < levels.length; i++) {
						let level = levels[i];
						for (var levelKey in level) {
							if(levelKey!=="levels"&&levelKey!=="id"&&levelKey!=="departmentCode"&&levelKey!="name"&&levelKey!=="universalSchedule"){
								let value = document.getElementById(String(levelKey) + String(i));
								if(value!==null){
									level[levelKey] = document.getElementById(String(levelKey) + String(i)).value;
								}else{
									level[levelKey] = "";
								}
							}
						}
						levels[i] = level;
					}
					currentData["levels"] = levels;
				}
			}
	
			let data = JSON.stringify(currentData);
			let req = new XMLHttpRequest();
			req.open("POST", "/admin/departments/set", true);
			req.setRequestHeader("content-Type", "application/json");
			req.onreadystatechange = function(){
				if(req.readyState == 4 && req.status==200){
					alert("Department " + currentData["name"] +" has been updated!");
					window.location = "/admin/departments";
				}else if(req.readyState == 4 && req.status != 200){
					alert("Something went wrong...")
				}
			}
			req.send(data);
		}
	}else{
		alert("Name can not be empty!");
	}
}

deleteBtn.onclick = function () {
	if (currentData != null) {
		for (var key in currentData) {
			if (key != "levels") {
				if(document.getElementById(key)!=null){
					currentData[key] = document.getElementById(key).value;
				}
			} else {
				let levels = currentData["levels"];
				for (let i = 0; i < levels.length; i++) {
					let level = levels[i][String(i)];
					for (var levelKey in level) {
						level[levelKey] = document.getElementById(String(levelKey) + String(i)).value;
					}
					levels[i] = level;
				}
				currentData["levels"] = levels;
			}
		}

		let data = JSON.stringify(currentData);
		let req = new XMLHttpRequest();
		req.open("POST", "/admin/departments/delete", true);
		req.setRequestHeader("content-Type", "application/json");
		req.onreadystatechange = function(){
			if(req.readyState == 4 && req.status==200){
				alert("Successfully deleted department " + currentData["name"] +"!");
				window.location = "/admin/departments";
				
			}else if(req.readyState == 4 && req.status != 200){
				alert("Something went wrong...")
			}
		}
		req.send(data);
	}
}

add.onclick = function () {
	
	update.style.display = "none";
	deleteBtn.style.display = "none";
	
	let multilevel = confirm("Do you want to create a multi-level department");
	content.innerHTML = "";
	if (multilevel) {
		addLevel.style.display = "inline-block";
		add.style.display = "none";
		globalFields(null, false);
		content.innerHTML += "<h2><b>Level " + levelCounter + "</b></h2></br>";
		insertLevelForm(null, levelCounter);
	} else {
		create.style.display = "inline-block";
		add.style.display = "none";
		insertForm(null);
	}
}

addLevel.onclick = function () {
	let fields = document.getElementsByClassName("data-field");
	let len = fields.length;
	let values = [];
	for(let i = 0; i < len; i++){
		values[i] = fields[i].value;
	}
	levelCounter++;
	create.style.display = "inline-block";
	content.innerHTML += "<h2><b>Level " + String(levelCounter) + "</b></h2><br>";
	insertLevelForm(null, levelCounter);
	for(let i = 0; i < len; i++){
		fields[i].value = values[i] 
	}
}

create.onclick = function () {
	if(document.getElementById("name").value !== ""){
		let dataObject = {};
		let fields = document.getElementsByClassName("data-field");
		let universal = document.getElementById("universalSchedule").value;
		if(universal == "true"){
			for (let i = 0; i < fields.length; i++) {
				let field = fields[i];
				if (field.value != "") {
					dataObject[field.id] = field.value;
				}
			}
		}else{
			dataObject["name"] = document.getElementById("name").value;
			dataObject["universalSchedule"] = document.getElementById("universalSchedule").value;
			dataObject["departmentCode"] = document.getElementById("departmentCode").value;
			let levels = [];
			for(let i = 0; i <= levelCounter; i++){
				
				let levelObj = {};
				for(let j = 0; j < fields.length; j++){
					if(fields[j].id.includes(String(i))){
						levelObj[fields[j].id.replace(/[0-9]/g, '')] = fields[j].value;
					}
				}
				levels[i] = levelObj;
				
			}
			levelCounter = 0;
			dataObject["levels"]=levels;
		}
		let data = JSON.stringify(dataObject);
		let req = new XMLHttpRequest();
			req.open("POST", "/admin/departments/set", true);
			req.setRequestHeader("content-Type", "application/json");
			req.onreadystatechange = function(){
				if(req.readyState == 4 && req.status==200){
					alert("Department " + dataObject["name"] +" has been created!");
					window.location = "/admin/departments";
				}else if(req.readyState == 4 && req.status != 200){
					alert("Something went wrong...")
				}
			}
			req.send(data);
	}else{
		alert("Name can not be empty!");
	}
 }

function globalFields(data, universal){
	
	let dptCode, name;
	if(data==null){
		dptCode = name = "";
	}else{
		dptCode = data["departmentCode"];
		name = data["name"];
	}

	let newForm = "";

	newForm += "<input class='data-field form-control' placeholder='Code' type='text' id='departmentCode' value='"+ dptCode +"'><small class='form-text text-muted'>Optional: Manually enter the department code</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Name' type='text' id='name' value='"+ name +"'><small class='form-text text-muted'>The name of the department</small><br>";
	
	newForm += "<select class='data-field form-control' id='universalSchedule' disabled>";
	let universalTrue = (universal === true) ? "selected" : "";
	let universalFalse = (universal === false) ? "selected" : "";
	newForm += "<option value='true' "+universalTrue+">Yes</option>";
	newForm += "<option value='false' "+universalFalse+">No</option></select>";
	newForm += "<small class='form-text text-muted'>Information applies for the entire department</small><br>";
	
	content.innerHTML = content.innerHTML + newForm;

}

function insertForm(data){
	globalFields(data, true);

	let breaks, dailyBreaks, shift, employee, monthly, schedule, daily, weekends;
	if(data === null){
		breaks=dailyBreaks=shift=employee=monthly=daily="";
		schedule = weekends = null;
	}else{
		breaks = data["breakBetweenShifts"];
		dailyBreaks = data["dailyBreakDurationTotal"];
		shift = data["shiftLength"];
		employee=data["employeesPerShift"];
		monthly=data["monthlyWorkDays"];
		daily = data["dailyHours"];
		schedule = data["scheduleType"];
		weekends = data["workOnWeekends"];
	}
	
	if(daily===null || daily === undefined)daily = "";

	let newForm = "";

	newForm += "<select class='data-field form-control' id='scheduleType' required>";
	let regularOption = (schedule === "Regular") ? "selected" : "";
	let irregularOption = (schedule === "Irregular") ? "selected" : "";
	let onCallOption = (schedule === "OnCall") ? "selected" : "";
	if(schedule===null){
		newForm += "<option value='' selected>Select..</option>";
	}
	newForm+="<option value='Regular' "+regularOption+">Regular</option>";
	newForm+="<option value='Irregular' "+irregularOption+">Irregular</option>";
	newForm+="<option value='OnCall' "+onCallOption+">On Call</option></select>";
	newForm += "<small class='form-text text-muted'>Type of scheduling for the department</small><br>";
		
	newForm += "<input class='data-field form-control' placeholder='Number of hours' type='number' id='breakBetweenShifts' value='"+ breaks+"'><small class='form-text text-muted'>Minimum break time between an employee\'s shifts</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of hours' type='number' id='dailyBreakDurationTotal' value='"+ dailyBreaks +"'><small class='form-text text-muted'>Break time per day</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Length of Shift' type='number' id='shiftLength' value='"+ shift +"'><small class='form-text text-muted'>Each employee's shift length</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of employees' type='number' id='employeesPerShift' value='"+ employee +"'><small class='form-text text-muted'>Number of employees for each shift</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of days' type='number' id='monthlyWorkDays' value='"+ monthly +"'><small class='form-text text-muted'>Total monthly work days per employee</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Start-hour, Start-minutes, End-hour, End-minutes' type='text' id='dailyHours' value='"+ daily +"'><small class='form-text text-muted'>Department's open hours</small><br>";
	
	newForm += "<select class='data-field form-control' id='workOnWeekends'/>";
	if(weekends===null){
		newForm += "<option value='' selected>Select..</option>";
	}
	let weekendTrue = (weekends === true) ? "selected" : "";
	let weekendFalse = (weekends === false) ? "selected" : "";
	newForm += "<option value='true' "+weekendTrue+">Yes</option>";
	newForm += "<option value='false' "+weekendFalse+">No</option></select>";
	newForm += "<small class='form-text text-muted'>Do the employees work on the weekends</small><br>";	
	
	content.innerHTML = content.innerHTML + newForm;

}

function insertLevelForm(data, counter){
	let breaks, dailyBreaks, shift, employee, monthly, schedule, daily, weekends;
	if(data == null){
		breaks=dailyBreaks=shift=employee=monthly=daily="";
		schedule = weekends = null;
	}else{
		breaks = data["levels"][counter]["breakBetweenShifts"];
		dailyBreaks = data["levels"][counter]["dailyBreakDurationTotal"];
		shift = data["levels"][counter]["shiftLength"];
		employee=data["levels"][counter]["employeesPerShift"];
		monthly=data["levels"][counter]["monthlyWorkDays"];
		daily = data["levels"][counter]["dailyHours"];
		schedule = data["levels"][counter]["scheduleType"];
		weekends = data["levels"][counter]["workOnWeekends"];

	}
	if(daily===null || daily === " ")daily = "";

	let newForm = "";

	newForm += "<select class='data-field form-control' id='scheduleType"+counter+"' required>";
	let regularOption = (schedule === "Regular") ? "selected" : "";
	let irregularOption = (schedule === "Irregular") ? "selected" : "";
	let onCallOption = (schedule === "OnCall") ? "selected" : "";
	if(weekends===null){
		newForm += "<option value='' selected>Select..</option>";
	}
	newForm+="<option value='Regular' "+regularOption+">Regular</option>";
	newForm+="<option value='Irregular' "+irregularOption+">Irregular</option>";
	newForm+="<option value='OnCall' "+onCallOption+">On Call</option></select>";
	newForm += "<small class='form-text text-muted'>Type of scheduling for the department</small><br>";
		
	newForm += "<input class='data-field form-control' placeholder='Number of hours' type='number' id='breakBetweenShifts"+counter+"' value='"+ breaks +"'><small class='form-text text-muted'>Minimum break time between an employee\'s shifts</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of hours' type='number' id='dailyBreakDurationTotal"+counter+"' value='"+ dailyBreaks +"'><small class='form-text text-muted'>Break time per day</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Length of Shift' type='number' id='shiftLength"+counter+"' value='"+ shift +"'><small class='form-text text-muted'>Each employee's shift length</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of employees' type='number' id='employeesPerShift"+counter+"' value='"+ employee +"'><small class='form-text text-muted'>Number of employees for each shift</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Number of days' type='number' id='monthlyWorkDays"+counter+"' value='"+ monthly +"'><small class='form-text text-muted'>Total monthly work days per employee</small><br>";
	newForm += "<input class='data-field form-control' placeholder='Start-hour, Start-minutes, End-hour, End-minutes' type='text' id='dailyHours"+counter+"' value='"+ daily +"'><small class='form-text text-muted'>Department's open hours</small><br>";
	
	newForm += "<select class='data-field form-control' id='workOnWeekends"+counter+"'/>";
	let weekendTrue = (weekends === true) ? "selected" : "";
	let weekendFalse = (weekends === false) ? "selected" : "";
	if(weekends===null){
		newForm += "<option value='' selected>Select..</option>";
	}
	newForm += "<option value='true' "+weekendTrue+">Yes</option>";
	newForm += "<option value='false' "+weekendFalse+">No</option></select>";
	newForm += "<small class='form-text text-muted'>Do the employees work on the weekends</small><br>";
	
	content.innerHTML += newForm;
	
}
