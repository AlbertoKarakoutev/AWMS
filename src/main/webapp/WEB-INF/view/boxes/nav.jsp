<div class="user-container">
    <div class="avatar-container">
        <div class="small-avatar"></div>
    </div>
    <div class="user-info-container">
        <div class="user-name">${employee.getFirstName()} ${employee.getLastName()}</div>
        <div class="user-email">${employee.getEmail()}</div>
    </div>
</div>
<ul class="vertical-menu">
    <li>
        <a href="#" title="Contacts">Contacts</a>
    </li>
    <li>
        <a href="#" title="Working Shedule">Working Shedule</a>
    </li>
    <li>
        <a href="#" title="Documents Access">Documents Access</a>
    </li>

    <li>
        <div class="dropdown">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true"
                aria-expanded="false" title="Forum">Forum</a>
            <div class="documents-dropdown dropdown-menu">
                <a class="dropdown-item" href="#">My topics</a>
                <a class="dropdown-item" href="#">Answered</a>
                <a class="dropdown-item" href="#">Unanswered</a>              
            </div>
        </div>
    </li>


</ul>
<div class="button">
    <a href="#" type="button" class="btn sign-out-btn btn-lg btn-block" role="button" aria-disabled="true"
        title="Sing out">SING OUT</a>
</div>
