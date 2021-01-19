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
                    <h1 class="ty-page-title">${thread != null ? "Edit Topic" : "Create Topic"}</h1>
                </header>
                <div class="my-3">
                    <form method="POST" action="${thread != null ? String.format("/forum/thread/%s/edit", thread.getID()) : '/forum/add'}">
                        <div class="form-group">
                            <label for="exampleInputEmail1">Title</label>
                            <input type="text" name="title" class="form-control" value="${thread != null ? thread.getTitle() : null}" id="threadTitle" aria-describedby="threadTitle" placeholder="Enter Title" required>
                            <small id="threadTitleHelp" class="form-text text-muted">Title</small>
                        </div>
                        <div class="form-group">
                            <label for="threadContent">Body</label>
<<<<<<< Updated upstream
                            <textarea name="body" class="form-control" id="threadContent" rows="8" required>${thread != null ? thread.getBody() : null}</textarea>
=======
                            <textarea name="body" class="form-control" id="threadContent" rows="8" required>${thread != null ? thread.getBody() : ""}</textarea>
>>>>>>> Stashed changes
                            <small id="threadContentHelp" class="form-text text-muted">Body</small>
                        </div>
                        <button type="submit" class="btn btn-dark">${thread != null ? "Edit": "Create"}</button>
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
