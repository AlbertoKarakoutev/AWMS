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

