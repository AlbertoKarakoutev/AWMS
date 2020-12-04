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
        <div class="dropdown">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" role="button"
                aria-haspopup="true" aria-expanded="false" title="Documents">Documents Access</a>
            <div class="documents-dropdown dropdown-menu">
                <a class="dropdown-item" href="#">All Documents</a>
                <a class="dropdown-item" href="#">Document1</a>
                <a class="dropdown-item" href="#">Document2</a>
                <a class="dropdown-item" href="#">Document3</a>
                <a class="dropdown-item" href="#">Document4</a>
            </div>
        </div>
    </li>

    <li>
        <a href="#" title="Forum">Forum</a>
    </li>   
</ul>
<div class="button">
    <a href="#" type="button" class="btn sign-out-btn btn-lg btn-block" role="button"
    aria-disabled="true" title="Sing out" >SING OUT</a>
</div>


