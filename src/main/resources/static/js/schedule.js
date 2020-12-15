var now = new Date();
var numberOfDays = new Date(now.getFullYear(), now.getMonth(), 0).getDate();
var btns = document.getElementsByClassName("day-box");
for(let i = 0; i <= numberOfDays; i++){
	btns[i].textContent = i+1;
}

function datePrompt(nationalID, receiverDate){
	let requesterDate = prompt("Enter date for the shift you wish to swap in the format /YYYY-MM-DD/", "2020-12-10");
	if(requesterDate!=null){
		let req = new XMLHttpRequest();
		req.open("GET", "/schedule/swapRequest/?receiverNationalID="+String(nationalID)+"&requesterDate="+requesterDate+"&receiverDate="+receiverDate, true);
		req.send();
	}
}

function deleteWorkDay(nationalID, date){
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/schedule/delete/?employeeNationalID="+String(nationalID)+"&date="+date, true);
	req.send();
	console.log(nationalID);
	console.log(date);
	let row = document.getElementById(String(nationalID));
	row.remove();
}

function getInput(modalID, date){
	let modal = document.getElementById("body"+modalID);
	modal.innerHTML += '<div class="md-form md-outline">'+
  										'<input type="time" id="start" class="form-control" placeholder="Select time">'+
										'<label for="start">Start Time</label>' +
  										'<input type="time" id="end" class="form-control" placeholder="Select time">'+
										'<label for="end">End Time</label>'+
										'<div class="input-group mb-3">'+
  										'<div class="input-group-prepend">'+
    									'<span class="input-group-text" id="inputGroup-sizing-default">National ID</span></div>'+
										'<input type="text" id="ID" class="form-control" aria-label="Default" aria-describedby="inputGroup-sizing-default"></div></div>'+
										'<td><button class="btn btn-dark" onclick=\'addWorkDay("'+date+'")\'>Create </button></td>';

}

function addWorkDay(date){
	let startShift = document.getElementById("start").value;
	let endShift = document.getElementById("end").value;
	let nationalID = document.getElementById("ID").value;
	console.log(date);
	let req = new XMLHttpRequest();
	req.open("GET", "/admin/schedule/add/?employeeNationalID="+String(nationalID)+"&date="+String(date)+"&startShift="+String(startShift)+"&endShift="+String(endShift), true);
	req.send();
	req.onload = function(){
		window.location.assign("/");
	}
}

