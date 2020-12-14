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
                    <h1 class="ty-page-title">Salary Info</h1>
                </header>
                <div class="py-3">
                    <div class="row">
                        <div class="col-12 col-lg-6">
                            <div class="row">
                                <div class="col-12 col-md-6">
                                    Total Salary: <span class="font-weight-bold">${salary}</span>
                                </div>
                                <div class="col-12 col-md-6">
                                    Work Hours: <span class="font-weight-bold">${workHours}</span>
                                </div>
                                <div class="col-12 col-md-6">
                                    Pay Per Hour: <span class="font-weight-bold">${payPerHour}</span>
                                </div>
                                <div class="col-12 col-md-6">
                                    Task Rewards: <span class="font-weight-bold">${taskRewards}</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-12 col-lg-6">
                            <div class="row"></div>
                        </div>
                    </div>
                </div>
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
