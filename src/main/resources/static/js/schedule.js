var parent = document.getElementsByClassName("parent")[0];

var employees = '${sameLevelEmployees}';
var now = new Date();
var firstWeekday = new Date(now.getFullYear(), now.getMonth(), 1).getDay()-1;
var numberOfDays = new Date(now.getFullYear(), now.getMonth(), 0).getDate()+1;

var modals = document.getElementsByClassName("modal");
var btns = document.getElementsByClassName("day-box");
var spans = document.getElementsByClassName("close");
var info = document.getElementsByClassName("info");
for(let i = 0; i <= 34; i++){
	
	if(i >= firstWeekday && i<= numberOfDays){

		btns[i].textContent = (i+1)-firstWeekday;
		
		btns[i].onclick = function () {
			modals[i].style.display = "block";
		}

		spans[i].onclick = function () {
			modals[i].style.display = "none";
		}
		
		window.onclick = function (event) {
			if (event.target == modals[i]) {
				modals[i].style.display = "none";
			}
		}
		
	}

}