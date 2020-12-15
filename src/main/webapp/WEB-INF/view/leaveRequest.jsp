<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" rel="stylesheet">

    <!--Add vendor's js files-->
    <script src="/webjars/jquery/3.5.1/jquery.js"></script>
    <script src="/webjars/bootstrap/4.5.3/js/bootstrap.bundle.js"></script>
    <script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script>
    <title>Request a Leave</title>
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
        <section class="content">
            <div class="p-4">
                <header class="py-3">
                    <h1 class="ty-page-title">Request a Leave</h1>
                </header>
                <div class="my-3">
                    <form action="/employee/requestLeave">
                        <div class="form-group">
                        	<div class="md-form">
  								<input placeholder="Select date" type="text" name="startDate" id="startDate" class="form-control datepicker" required>
							  	<small id="startDate" class="form-text text-muted">Start Date</small>
							</div>
                        </div>
                        <div class="form-group">
                        	<div class="md-form">
  								<input placeholder="Select date" type="text" name="endDate" id="endDate" class="form-control datepicker" required>
  								<small id="endDate" class="form-text text-muted">End Date</small>
							</div>
                        </div>
                        <label for="paid">Paid?</label>
                        <select class="form-control" name="paidStr" id="paid">
                        	<option value="true">Yes</option>
                        	<option value="false" selected>No</option>
                        </select>
                        <button type="submit" class="btn btn-dark">Request</button>
                    </form>
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
