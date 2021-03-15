window.onload = function(){
	
	var btns = document.getElementsByClassName("day-box");
	for(let i = 0; i <= btns.length; i++){
		if(btns[i]!==undefined){
			btns[i].insertAdjacentHTML("afterbegin", i+1); 
		}
	}
}

function getDayData(day){
	let req = new XMLHttpRequest();
	let taskReq = new XMLHttpRequest();
	let url = new URLSearchParams(location.search);
	let dayDate = (day<10) ? url.get("month") + "-0" + day : url.get("month") + "-" + day;
	req.open("GET", "/schedule/day/?dateStr="+dayDate, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status==200){
			
			let res = JSON.parse(req.response);
			let role = res["role"];
			let button="";

			let employeeModalBody  = document.getElementById("day-modal-body");
			employeeModalBody.innerHTML = "";
			for(let i = 0; i < res["employees"].length; i++){
				if(role === "ADMIN"){
					let thisDate = Date.parse(dayDate);
					if(thisDate >= Date.now()){
						button = "<td><button class='btn btn-dark' onclick=\"deleteWorkDay('"+i+"', '"+res["employees"][i]["nationalID"]+"', '"+String(dayDate)+"')\">Delete</button></td>"
					}
				}else if(role === "EMPLOYEE"){
					let thisDate = Date.parse(dayDate);
					if(thisDate >= Date.now()){
						let workDay = res["workDayForEmployee"];
						if(workDay===false){
							button = "<td><button class='btn btn-dark' data-dismiss='modal' data-toggle='modal' data-target='#swap-modal' onclick=\"getSwapRequestData('"+res["employees"][i]["nationalID"]+"', '"+String(dayDate)+"')\">Swap Shifts</button></td>"
						}
					}
				}else if(role ==="MANAGER"){
					let thisDate = Date.parse(dayDate);
					if(thisDate >= Date.now()){
						button = "<td><button class='btn btn-dark' onclick='getTaskData(\""+res["employees"][i]["nationalID"]+"\" , \""+String(dayDate)+"\" , \""+i+"\")' >Add a Task</button></td>";
					}
				}
				let newRow = "<td>"+res["employees"][i]["firstName"] + " " +res["employees"][i]["lastName"] +"</td><td>"+res["employees"][i]["workTimeInfo"]+"<td>"+button;
				employeeModalBody.insertAdjacentHTML("beforeend", "<tr id='employee-row-"+i+"'>"+newRow+"</tr");
			}
			if(role === "ADMIN"){
				let thisDate = Date.parse(dayDate);
				if(thisDate >= Date.now()){
					let addButtonHTML = "<tr id='addWorkDayButton'><th></th><td><button class='btn btn-dark' onclick='getWorkDayData(\""+String(dayDate)+"\")'>Add</button></td><td></td><tr>"
					employeeModalBody.insertAdjacentHTML("beforeend",addButtonHTML);
				}
			}
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...")
		}
	}
	req.send();

	taskReq.open("GET", "/schedule/day/tasks/?dateStr="+dayDate, true);
	taskReq.onreadystatechange = function(){
		if(taskReq.readyState == 4 && taskReq.status==200){
				
			let taskRes = JSON.parse(taskReq.response);
			if(taskRes.length<1){
				document.getElementById("task-table").style.display="none";
			}else{
				document.getElementById("task-table").style.display="table";
			}
			let taskModalBody  = document.getElementById("task-modal-body");
			taskModalBody.innerHTML = "";
			
			for(let i = 0; i < taskRes.length; i++){
				let title = "<th scope='row'><h4>"+taskRes[i]["taskTitle"]+"</h4></th>";
				let body = "<td><h4>"+taskRes[i]["taskBody"]+"</h4></td>";
				let reward = "<td><h4>"+taskRes[i]["taskReward"]+"</h4></td>";
				let status;
				if(taskRes[i]["completed"]!==true){
					status = "<td style='text-align:center' id='status'><button class='btn btn-dark' onclick='markTaskAsComplete(\""+i+"\", \""+dayDate+"\")'>Mark as Complete</button></td>";
				}else if(taskRes[i]["paidFor"]===true){
					status = "<td style='text-align:center' id='status'><i class='btn fas fa-lg fa-check'></i><i class='btn fas fa-lg fa-comment-dollar'></i></td>";
				}else{
					status = "<td style='text-align:center' id='status'><i class='btn fas fa-lg fa-check'></i></td>";
				}
				taskModalBody.insertAdjacentHTML("afterbegin", "<tr>"+title+body+reward+status+"</tr>");
			}
		}
	}
	taskReq.send();
}

function getSwapRequestData(nationalID, receiverDate){
	let swapRequestBtn = document.getElementById("swap-request-btn");
	let dates = document.getElementById("dates");
	dates.innerHTML = "";
	dates.disabled = false;
	swapRequestBtn.style.display = "block";
	let req = new XMLHttpRequest();
	req.open("GET", "/schedule/upcoming/?dateStr="+receiverDate+"&receiverNationalID="+nationalID, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			let schedule = JSON.parse(req.response);
			if(schedule.length>0){
				for(let i = 0; i < schedule.length; i++){
					let row =  "<option value='"+schedule[i]+"'>"+schedule[i]+"</option>";
					dates.insertAdjacentHTML("beforeend", row);
					let temp = dates.innerHTML;
					dates.innerHTML = temp;
				}
			}else{
				dates.insertAdjacentHTML("beforeend",  "<option selected>No available dates!</option>");
				dates.disabled = true;
				swapRequestBtn.style.display = "none";
			}
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...")
		}
	}
	req.send();
	let receiverNationalIDElem = document.getElementById("receiverNationalID");
	let receiverDateElem = document.getElementById("receiverDate");
	receiverNationalIDElem.value = String(nationalID);
	receiverDateElem.value = String(receiverDate);
}

function sendSwapRequest(){
	let receiverNationalID = document.getElementById("receiverNationalID").value;
	let receiverDate = document.getElementById("receiverDate").value;
	let requesterDate = document.getElementById("dates").value;
	let req = new XMLHttpRequest();
	req.open("GET", "/schedule/swapRequest/?receiverNationalID="+receiverNationalID+"&receiverDate="+receiverDate+"&requesterDate="+requesterDate, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status==200){
			alert("Successfully sent a swap request!");
		}else if(req.readyState == 4 && req.status == 400){
			alert("You already have a shift in that day!");
		}else if(req.readyState == 4){
			alert("Something went wrong...")
		}
		document.getElementById("receiverNationalID").value = "";
		document.getElementById("receiverDate").value = "";
		document.getElementById("receiverDate").value = "";
	}
	req.send();
}

function deleteWorkDay(employee, nationalID, date){
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/schedule/delete/?employeeNationalID="+String(nationalID)+"&date="+date, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			let row = document.getElementById("employee-row-"+String(employee));
			row.remove();
			alert("Successfully deleted the employee's " + String(date) + " shift!");
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...");
		}
	}
	req.send();
}

function getTaskData(nationalID, date, row){
	console.log(String(nationalID));
	let modalRow = document.getElementById("employee-row-"+row);
	if(document.getElementById("title")==null){
		let newForm='<td>'+
					'<input type="text" class="form-control" id="title" name="title" placeholder="Title" required>'+
					'<input type="text" class="form-control" id="body" name="body" rows="3" placeholder="Body" required>'+
					'<input type="number" class="form-control" id="reward" name="reward" placeholder="Reward" required>'+
					'<input type="text" class="form-control" id="taskDate" name="date" value="'+String(date)+'" hidden>'+
					'<input type="text" class="form-control" id="taskReceiverNationalID" name="receiverNationalID" value="'+String(nationalID)+'" hidden>'+
					'<button onclick="addTask()" class="form-control btn btn-dark">Add</button></td>';
		modalRow.insertAdjacentHTML('afterend', newForm);
		
	}
}

function addTask(){
	let title = document.getElementById("title").value;
	let body = document.getElementById("body").value;
	let reward = String(document.getElementById("reward").value);
	let date = document.getElementById("taskDate").value;
	let receiverNationalID = document.getElementById("taskReceiverNationalID").value;
	let reqBody = "receiverNationalID="+receiverNationalID+"\ndate="+String(date)+"\nreward="+reward+"\nbody="+body+"\ntitle="+title;
	let req = new XMLHttpRequest();
	req.open("POST", "/schedule/addTask", true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			alert("Successfully added this task!");
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...");
		}
		document.getElementById("title").value = "";
		document.getElementById("body").value = "";
		document.getElementById("reward").value = "";
		document.getElementById("taskDate").value = "";
		document.getElementById("taskReceiverNationalID").value = "";
	}
	req.send(reqBody);
}

function markTaskAsComplete(taskNum, date){
	let req = new XMLHttpRequest();
	req.open("GET", "/schedule/taskComplete/?taskNum="+String(taskNum)+"&date="+date, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			document.getElementById("status").innerHTML = '<i class="btn fas fa-lg fa-check"></i>';
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...");
		}
	}
	req.send();
}	

function getWorkDayData(date){
	let modal = document.getElementById("day-modal-body");
	let buttonRow = document.getElementById("addWorkDayButton");
	buttonRow.remove();
	modal.innerHTML +=  '<tr><th></th><td><div class="md-form md-outline">'+
						'<input type="time" id="start" class="form-control" placeholder="Select time">'+
						'<label for="start">Start Time</label>' +
						'<input type="time" id="end" class="form-control" placeholder="Select time">'+
						'<label for="end">End Time</label>'+
						'<div class="input-group mb-3">'+
						'<div class="input-group-prepend">'+
						'<span class="input-group-text" id="inputGroup-sizing-default">National ID</span></div>'+
						'<input type="text" id="ID" class="form-control" aria-label="Default" aria-describedby="inputGroup-sizing-default"></div></div>'+
						'<button class="btn btn-dark" onclick=\'addWorkDay("'+date+'")\'>Create </button></td><td></td></tr>';


}

function addWorkDay(date){
	console.log(date);
	let startShift = document.getElementById("start").value;
	let endShift = document.getElementById("end").value;
	let nationalID = document.getElementById("ID").value;
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/schedule/add/?employeeNationalID="+String(nationalID)+"&date="+String(date)+"&startShift="+String(startShift)+"&endShift="+String(endShift), true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			alert("Successfully added the " + String(date) + " work day!");
			location.reload();
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...");
		}
		document.getElementById("start").value = "";
		document.getElementById("end").value = "";
		document.getElementById("ID").value = "";
	}
	req.send();
}

