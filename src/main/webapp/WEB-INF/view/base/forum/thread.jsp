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
    <title>Тема - ${thread.getTitle()}</title>
</head>
<body>
    <div class="panel">
        <nav class="navigation">
            <%@include file="../../boxes/nav.jsp" %>
        </nav>
        <section class="page">
            <header class="header">
                <%@include file="../../boxes/header.jsp" %>
            </header>
            <section class="content">
                <div class="p-4">
                <header class="py-3">
                    <h1 class="ty-page-title">Title: ${thread.getTitle()}</h1>
                    <c:if test="${thread.getAnswered() == true}">
                        <h2>Answered</h2>
                    </c:if>
                </header>
                <div class="my-3 d-flex">
                    <div class="mr-2 avatar-container">
                        <div class="middle-avatar"></div>
                    </div>
                    <div class="w-100">
                        <div class="card w-100">
                            <c:if test="${not empty thread.getIssuerName()}">
                                <div class="card-header">
                                    <p><b>Issuer:</b> ${thread.getIssuerName()}</p>
                                </div>
                            </c:if>
                            <div class="card-body">
                                <c:out value="${thread.getBody()}" />
                            </div>
                            <c:if test="${not empty thread.getDateTime()}">
                                <div class="card-footer text-muted">
                                    <p><b>Created on:</b> ${thread.getDateTime()}</p>
                                </div>
                            </c:if>
                        </div>
                        <div class="d-flex mt-3 text-right align-items-center">
                            <c:if test="${thread.getIssuerID().equals(employeeID)}">
                                <a href="/forum/thread/${thread.getID()}/edit" class="btn btn-dark btn-md ml-auto">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                                <c:if test="${thread.getAnswered() == false}">
                                    <form action="${thread.getID()}/answered" method="post" class="m-0">
                                        <input type="submit" class="btn btn-dark btn-md mx-2" value="Mark as answered" />
                                    </form>
                                </c:if>
                            </c:if>
                            <a class="btn btn-dark btn-md mx-2" href="/forum/thread/${thread.getID()}/reply/new" title="reply">
                                <i class="fas fa-reply"></i> Add Reply
                            </a>
                        </div>
                        <br />
                        <c:forEach items="${replies}" var="reply">
                            <div class="card w-100 my-2">
                                <c:if test="${not empty reply.getIssuerName()}">
                                    <div class="card-header">
                                        <p><b>From:</b> ${reply.getIssuerName()}</p>
                                    </div>
                                </c:if>
                                <div class="card-body">
                                    <c:out value="${reply.getBody()}" />
                                </div>
                                <c:if test="${not empty reply.getDateTime()}">
                                    <div class="card-footer text-muted">
                                        <p><b>Added:</b> ${reply.getDateTime()}</p>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                </div>
                <footer>
                    <%@include file="../../boxes/footer.jsp" %>
                </footer>
            </section>
        </section>
    </div>
    <script type="module" src="/js/main.js"></script>
</body>
</html>
