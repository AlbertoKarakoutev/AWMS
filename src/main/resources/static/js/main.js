$(document).ready(function() {

    /*Toggle navigation*/
    $('.navbar-toggler').on('click', function(){
        let navigation = document.getElementById('navigation');
        let navbar = document.querySelector('.navbar');

        if(navigation.classList.contains('show')) {
            navbar.classList.remove('show');

            let backdrop = document.querySelector('.navbar-backdrop');
            
            document.body.removeChild(backdrop);
        } else {
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

    // $('#login').submit(function(e) {
    //     var response = grecaptcha.getResponse();
    //     if(response.length == 0) {
    //         e.preventDefault();
    //         alert('Please prove that you are not a robot.')
    //     }
    // });
});
