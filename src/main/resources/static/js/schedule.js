var parent = document.getElementsByClassName("parent")[0];

var now = new Date();
var numberOfDays = new Date(now.getFullYear(), now.getMonth(), 0).getDate();
var modals = document.getElementsByClassName("modal");
var btns = document.getElementsByClassName("day-box");
var spans = document.getElementsByClassName("close");
var info = document.getElementsByClassName("info");
for(let i = 0; i <= numberOfDays; i++){
	btns[i].textContent = i+1;
	
	btns[i].onclick = function () {
		modals[i].style.display = "block";
	}

	spans[i].onclick = function () {
		modals[i].style.display = "none";
	}
}

function datePrompt(nationalID, receiverDate){
	let requesterDate = prompt("Enter date for the shift you wish to swap in the format /YYYY-MM-DD/", "2020-12-10");
	if(requesterDate!=null){
		let req = new XMLHttpRequest();
		req.open("GET", "/schedule/swapRequest/?receiverNationalID="+nationalID+"&requesterDate="+requesterDate+"&receiverDate="+receiverDate, true);
		req.send();
	}
}

