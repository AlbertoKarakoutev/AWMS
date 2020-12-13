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
    <title>Forum</title>
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
                    <h1 class="ty-page-title">Change Password</h1>
                </header>
                <div class="my-3">
                    <form method="POST" action="/employee/password">
                        <div class="form-group">
                            <label for="newPassword">New Password</label>
                            <input type="password" name="newPassword" class="form-control" id="newPassword" aria-describedby="newPassword" placeholder="New Password" required>
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">Confirm Password</label>
                            <input type="password" name="confirmPassword" class="form-control" id="confirmPassword" aria-describedby="confirmPassword" placeholder="Confirm Password" required>
                        </div>
                        <button type="submit" class="btn btn-dark">Change Password</button>
                        <c:if test='${mismatch == true}'>Password Mismatch! Try again.</c:if>
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
