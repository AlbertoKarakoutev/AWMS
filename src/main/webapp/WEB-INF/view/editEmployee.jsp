<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" rel="stylesheet">

    <!--Add vendor's js files-->
    <script src="/webjars/jquery/3.5.1/jquery.js"></script>
    <script src="/webjars/bootstrap/4.5.3/js/bootstrap.bundle.js"></script>
    <script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script> 
	<title>Employees</title>
</head>
<body>
    <div class="panel">
        <%@include file="boxes/nav.jsp" %>    
        <section class="page">
            <header class="header">
                <%@include file="boxes/header.jsp" %>
            </header>
            <section class="content">
                <div class="p-4">
                    <header class="py-3">   
                        <h1 class="ty-page-title">
                            <c:if test="${newEmployee == true}">
                                Add new employee   
                            </c:if>
                            <c:if test="${!newEmployee == true}">
                                Now editing ${employee.getFirstName()} ${employee.getLastName()}!
                            </c:if>
                        </h1>
                    </header>
                    <form action="${newEmployee == true ? '/admin/employee/register' : '/admin/employee/update/?employeeId=${employee.getID()}'}" method="POST" enctype="text/plain">
                        <div class="form-group">
                            <label for="firstNameEmployee">First name</label>
                            <input value="${employee.getFirstName()}" type="text" name="firstName" class="form-control" id="firstNameEmployee" aria-describedby="firstName" placeholder="First name" required>
                            <small id="firstNameHelp" class="form-text text-muted">Employee's first name.</small>
                        </div>
                        <div class="form-group">
                            <label for="familyNameEmployee">Family name</label>
                            <input value="${employee.getLastName()}" type="text" name="familyName" class="form-control" id="familyNameEmployee" aria-describedby="familyName" placeholder="Family name">
                            <small id="familyNameHelp" class="form-text text-muted">Employee's family name.</small>
                        </div>
                        <div class="form-group">
                            <label for="nationalID">National ID</label>
                            <input value="${employee.getNationalID()}" type="text" name="nationalID" class="form-control" id="nationalIDnationalID" aria-describedby="firstName" placeholder="National ID" required>
                            <small id="nationalIDhelp" class="form-text text-muted">Employee's national ID number.</small>
                        </div>
                        <div class="form-group">
                            <label for="emailEmployee">Email</label>
                            <input value="${employee.getEmail()}" type="email" name="email" class="form-control" id="emailEmployee" aria-describedby="emailName" placeholder="Email" required>
                            <small id="emailHelp" class="form-text text-muted">Employee's email.</small>
                        </div>
                        <div class="form-group">
                            <label for="phoneNumberEmployee">Phone number</label>
                            <input value="${employee.getPhoneNumber()}" type="tel" name="phoneNumber" class="form-control" id="phoneNumberEmployee" aria-describedby="phoneNumberEmployee" placeholder="Phone number" required>
                            <small id="phoneNumberHelp" class="form-text text-muted">Employee's phone number.</small>
                        </div>
                        <div class="form-group">
                            <label for="ibanEmployee">IBAN</label>
                            <input value="${employee.getIBAN()}" type="tel" name="iban" class="form-control" id="ibanEmployee" aria-describedby="ibanEmployee" placeholder="Password" required>
                            <small id="ibanHelp" class="form-text text-muted">Employee's IBAN.</small>
                        </div>
                        <div class="form-group">
                            <label for="departmentEmployee">Department</label>
                            <select class="form-control" name="department" id="departmentEmployee">
                                <c:forEach items="${departments.keySet()}" var="key">
                                    <option selected="${key == employee.getDepartment()}">${key}:${departments.get(key)}</option>
                                </c:forEach>
                            </select>
                            <small id="departmentHelp" class="form-text text-muted">Employee's department.</small>
                        </div>
                        <div class="form-group">
                            <label for="levelEmployee">Level</label>
                            <input value="${employee.getLevel()}" type="number" name="level" class="form-control" id="levelEmployee" aria-describedby="levelEmployee" placeholder="Level" required>
                            <small id="levelHelp" class="form-text text-muted">Employee's department level.</small>
                        </div>
						<c:if test="${newEmployee == false}">
	                        <div class="form-group">
	                            <a class="btn btn-primary" href="/admin/personal/${employee.getID()}" title="Edit employee's files">Edit Files</a>
	                        </div>
	                        <button type="submit" class="btn btn-dark">Update</button>
                        </c:if>
                        <c:if test="${newEmployee == true}">
	                        <button type="submit" class="btn btn-dark">Register</button>
                        </c:if>
                    </form>
                </div>
                <footer>
                    <%@include file="boxes/footer.jsp" %>
                </footer>
            </section>
        </section>
    </div>
<script type="module" src="/js/main.js"></script>
</body>
</html>
