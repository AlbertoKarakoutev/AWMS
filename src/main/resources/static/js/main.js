$(document).ready(function() {
<<<<<<< HEAD
    $('.navbar-toggler').on('click', function(){
        let navigation = document.getElementById('navigation');
        let navbar = document.querySelector('.navbar');

        if(navigation.classList.contains('show')) {
            navigation.classList.remove('show');
            navbar.classList.remove('show');

            let backdrop = document.querySelector('.navbar-backdrop');
            
            document.body.removeChild(backdrop);
        } else {
            navigation.classList.add('show');
            navbar.classList.add('show');

            let backdrop = document.createElement('div');
            backdrop.classList.add('navbar-backdrop');
            document.body.appendChild(backdrop);

            backdrop.addEventListener('click', function(){
                navigation.classList.remove('show');
                navbar.classList.remove('show');
                document.body.removeChild(backdrop);
            });
        }
    });
});
=======
  	function calendarClick(element){
		console.log(element);
	  }
});
>>>>>>> 4efe0f2b031dc77627cc1d6cd4953e62f0500dfa
