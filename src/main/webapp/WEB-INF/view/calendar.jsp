<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script
        src="https://code.jquery.com/jquery-3.5.1.min.js"
        integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
        crossorigin="anonymous">
    </script>  
	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <link href="css/main.css" rel="stylesheet">
    <link href="css/components/calendar.css" rel="stylesheet">
    <script src="assets/js/main.js"></script>

	<title>Calendar</title>
</head>
<body>
    <div class="panel">
        <nav class="navigation">
            <%@include file="boxes/nav.jsp" %>
        </nav>       
        <section class="page">
            <header class="header">
                <%@include file="boxes/header.jsp" %>
            </header>
            <section class="p-4 content">
                <div class="working-shedule">Working Shedule</div>
                <div class="parent">
                    <div class="monday">Monday</div>
                    <div class="tuesday">Tuesday</div>
                    <div class="wednesday">Wednesday</div>
                    <div class="thursday">Thursday</div>
                    <div class="friday">Friday</div>
                    <div class="saturday">Saturday</div>
                    <div class="sunday">Sunday</div>
                    <div class="day-box day1">01</div>
                    <div class="day-box day2">02</div>
                    <div class="day-box day3">03</div>
                    <div class="day-box day4">04</div>
                    <div class="day-box day5">05</div>
                    <div class="day-box day6">06</div>
                    <div class="day-box day7">07</div>
                    <div class="day-box day8">08</div>
                    <div class="day-box day9">09</div>
                    <div class="day-box day10">10</div>
                    <div class="day-box day11">11</div>
                    <div class="day-box day12">12</div>
                    <div class="day-box day13">13</div>
                    <div class="day-box day14">14</div>
                    <div class="day-box day15">15</div>
                    <div class="day-box day16">16</div>
                    <div class="day-box day17">17</div>
                    <div class="day-box day18">18</div>
                    <div class="day-box day19">19</div>
                    <div class="day-box day20">20</div>
                    <div class="day-box day21">21</div>
                    <div class="day-box day22">22</div>
                    <div class="day-box day23">23</div>
                    <div class="day-box day24">24</div>
                    <div class="day-box day25">25</div>
                    <div class="day-box day26">26</div>
                    <div class="day-box day27">27</div>
                    <div class="day-box day28">28</div>
                    <div class="day-box day29">29</div>
                    <div class="day-box day30">30</div>
                    <div class="day-box day31">31</div>
                </div>
            </section>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
    </div>

</body>
</html>
