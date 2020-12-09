<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" rel="stylesheet">

    <!--Add vendor's js files-->
    <script src="/webjars/jquery/3.5.1/jquery.js"></script>
    <script src="/webjars/bootstrap/4.5.3/js/bootstrap.bundle.js"></script>
    <script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script> 
    <title>Отговор - Тема - ${threadTitle}</title>
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
                    <h1 class="ty-page-title">Отговор на ${threadTitle}</h1>
                </header>
                <div class="my-3">
                <form method="POST" action="/forum/thread/${threadID}/add">
                    <div class="form-group">
                        <label for="threadContent">Текст</label>
                        <textarea name="body" class="form-control" id="threadContent" rows="8" required></textarea>
                        <small id="threadContentHelp" class="form-text text-muted">Тема</small>
                    </div>
                    <button type="submit" class="btn btn-dark">Отговори</button>
                    </form>
                </div>
            </section>
        </section>
    </div>
</body>
</html>