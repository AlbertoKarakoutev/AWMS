var now = new Date();
var numberOfDays = new Date(now.getFullYear(), now.getMonth(), 0).getDate();
var btns = document.getElementsByClassName("day-box");
for(let i = 0; i <= numberOfDays; i++){
	let content = btns[i].innerHTML;
	btns[i].innerHTML = (i+1) + content;
}

function getSwapRequestData(nationalID, receiverDate){
	let receiverNationalIDElem = document.getElementById("receiverNationalID");
	let receiverDateElem = document.getElementById("receiverDate");
	receiverNationalIDElem.value = String(nationalID);
	receiverDateElem.value = String(receiverDate);
}

function sendSwapRequest(){
	let receiverNationalID = document.getElementById("receiverNationalID").value;
	let receiverDate = document.getElementById("receiverDate").value;
	let requesterDate = document.getElementById("receiverDate").value;
	let req = new XMLHttpRequest();
	req.open("GET", "/schedule/swapRequest/?receiverNationalID="+receiverNationalID+"&receiverDate="+receiverDate+"&requesterDate="+requesterDate, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status==200){
			alert("Successfully sent a swap request!");
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...")
		}
		document.getElementById("receiverNationalID").value = "";
		document.getElementById("receiverDate").value = "";
		document.getElementById("receiverDate").value = "";
	}
	req.send();
}

function deleteWorkDay(modalID, nationalID, date){
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/schedule/delete/?employeeNationalID="+String(nationalID)+"&date="+date, true);
	req.onreadystatechange = function(){
		if(req.readyState == 4 && req.status == 200){
			let row = document.getElementById(String(modalID)+"-"+String(nationalID));
			row.remove();
			alert("Successfully deleted the employee's " + String(date) + " shift!");
		}else if(req.readyState == 4 && req.status != 200){
			alert("Something went wrong...");
		}
	}
	req.send();
}

function getTaskData(modalID, nationalID, date){
	let modalRow = document.getElementById(String(modalID)+"-"+String(nationalID));
	let newForm='<td>'+
				'<input type="text" class="form-control" id="title" name="title" placeholder="Title" required>'+
				'<input type="text" class="form-control" id="body" name="body" rows="3" placeholder="Body" required>'+
				'<input type="number" class="form-control" id="reward" name="reward" placeholder="Reward" required>'+
				'<input type="text" class="form-control" id="taskDate" name="date" value="'+String(date)+'" hidden>'+
				'<input type="text" class="form-control" id="taskReceiverNationalID" name="receiverNationalID" value="'+String(nationalID)+'" hidden>'+
				'<button onclick="addTask()" class="form-control btn btn-dark">Add</button></td>';
	modalRow.insertAdjacentHTML('afterend', newForm);
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

function getWorkDayData(modalID, date){
	let modal = document.getElementById("body"+modalID);
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

