<%@page import="java.time.LocalDate"%>
<nav class="navbar navbar-expand-lg">
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navigation" aria-controls="navigation" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon">
        <i class="fas fa-bars"></i>
    </span>
  </button>
  <div class="collapse navbar-collapse" id="navigation">
  <header class="user-container p-2">
    <div class="avatar-container">
        <div class="small-avatar"></div>
    </div>
    <div class="user-info-container">
        <div class="user-name">${employeeName}</div>
        <div class="user-email">${employeeEmail}</div>
    </div>
   </header>
    <ul class="vertical-menu">
    <li class="vertical-item">
        <a href="/" title="Dashboard">Dashboard</a>
    </li>
    <li class="vertical-item">
        <a href="/contacts" title="Contacts">Contacts</a>
    </li>
    <li class="vertical-item">
        <a href="/schedule/?month=<%=LocalDate.now().getMonthValue()%>" title="Working Shedule">Schedule</a>
    </li>
    <li class="vertical-item">
        <div class="dropdown">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" role="button"
                aria-haspopup="true" aria-expanded="false" title="Forum">Forum</a>
            <div class="dropdown-menu">
                <a class="dropdown-item pl-4" title="Module" href="/forum">All topics</a>
                <a class="dropdown-item pl-4" title="Module" href="/forum/employee/threads/${employeeID}">My topics</a>
                <a class="dropdown-item pl-4" title="Module" href="/forum/answered">Answered</a>
                <a class="dropdown-item pl-4" title="Module" href="/forum/unanswered">Unanswered</a>
               
            </div>
        </div>
    </li>

    <li class="vertical-item">
        <a href="#" title="Documents">Documents Access</a>
    </li>   
    </ul>
    <footer class="button">
        <a href="/logout" type="button" class="btn sign-out-btn" title="Sing out" >SING OUT</a>
    </footer>
  </div>
</nav>
