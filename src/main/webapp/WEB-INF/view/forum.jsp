<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" media="all" rel="stylesheet">
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
            <section class="p-4 content">
                <header class="py-3">
                    <h1 class="ty-page-title">Forum</h1>
                </header>
                <div class="py-3">
                    <div class="d-flex flex-row-reverse">
                        <button class="btn ap-button">New question</button>
                    </div>
                </div>
                <div class="my-3">
                    <div class="forum-questions">
                        <h3 class="text-light">Do you smoke?</h2>
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
<script src="/webjars/bootstrap/4.5.1/js/bootstrap.js"></script>
<script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script>
<script src="/js/main.js"></script>
</body>
