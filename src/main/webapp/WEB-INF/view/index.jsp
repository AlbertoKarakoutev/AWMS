<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="webjars/bootstrap/4.5.3/css/bootstrap.min.css" rel="stylesheet" />
    <link href="css/main.css" rel="stylesheet">

	<title>Insert title here</title>
</head>
<body>
    <div class="panel">
        <nav class="navigation">
            <%@include file="boxes/nav.jsp" %>
        </nav>
        <section class="page">
            <header>
                <%@include file="boxes/header.jsp" %>
            </header>
            <section class="p-2">
                <h2 align="center"> Hello ${name}!</h2>
                <h2>Current Time: ${time}</h2>
                <div class="btn btn-primary">Button</div>
            </section>
        </section>
    </div>

<!--Add main js file-->
<script src="webjars/jquery/3.5.1/jquery.js"></script>
<script src="webjars/popper.js/1.16.0/umd/popper.js"></script>
<script src="webjars/bootstrap/4.5.3/js/bootstrap.js"></script>
<script src="assets/js/main.js"></script>
</body>
</html>
