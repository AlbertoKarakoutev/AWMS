<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" rel="stylesheet">

	<title>Dashboard</title>
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
                <header class="py-3">
                    <h1 class="ty-page-title">Welcome, ${employee.getFirstName()} ${employee.getLastName()}!</h1>
                </header>
                <div class="py-3">
                    <div class="avatar"></div>
                </div>
                <div class="py-3">
                <div class="row">
                    <div class="col-12 col-lg-6">
                        <div class="row">
                        <div class="col-12 col-md-6">
                            Name: <span class="font-weight-bold">${employee.getFirstName()} ${employee.getLastName()}</span>
                        </div>
                        <div class="col-12 col-md-6">
                            Role: <span class="font-weight-bold">${employee.getRole()}</span>
                        </div>
                        <div class="col-12 col-md-6">
                            Natinal ID: <span class="font-weight-bold">${employee.getNationalID()}</span>
                        </div>
                        <div class="col-12 col-md-6">
                            Salary: <span class="font-weight-bold">${employee.getSalary()}</span>
                        </div>
                        <div class="col-12 col-md-6">
                            Phone Number: <span class="font-weight-bold">${employee.getPhoneNumber()}</span>
                        </div>
                        <div class="col-12 col-md-6">
                            IBAN: <span class="font-weight-bold">${employee.getIBAN()}</span>
                        </div>
                        </div>
                    </div>
                    <div class="col-12 col-lg-6">
                        <div class="row"></div>
                    </div>
                </div>
                </div>
            </section>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
    </div>

<!--Add main js file-->
<script src="/webjars/jquery/3.5.1/jquery.js"></script>
<script src="/webjars/popper.js/2.5.2/umd/popper.js"></script>
<script src="/webjars/bootstrap/4.5.3/js/bootstrap.js"></script>
<script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script>
<script src="/js/main.js"></script>
</body>
</html>
