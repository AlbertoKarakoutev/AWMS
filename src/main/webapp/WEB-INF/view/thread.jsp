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
            <%@include file="boxes/nav.jsp" %>
        </nav>
        <section class="page">
            <header class="header">
                <%@include file="boxes/header.jsp" %>
            </header>
            <section class="content">
                <div class="p-4">
                <header class="py-3">
                    <h1 class="ty-page-title">Тема: ${thread.getTitle()}</h1>
                </header>
                <div class="my-3 d-flex">
                    <div class="mr-2 avatar-container">
                        <div class="middle-avatar"></div>
                    </div>
                    <div class="w-100">
                        <div class="card w-100">
                            <c:if test="${not empty thread.getIssuerName()}">
                                <div class="card-header">
                                    <p><b>Създадена от:</b> ${thread.getIssuerName()}</p>
                                </div>
                            </c:if>
                            <div class="card-body">
                                <c:out value="${thread.getBody()}" escapeXml="false" />
                            </div>
                            <c:if test="${not empty thread.getDateTime()}">
                                <div class="card-footer text-muted">
                                    <p><b>Създадена на:</b> ${thread.getDateTime()}</p>
                                </div>
                            </c:if>
                        </div>
                        <div class="mt-2 text-right">
                            <c:if test="${thread.getIssuerID().equals(employeeID)}">
                                <a href="/forum/thread/${thread.getID()}/edit" class="btn btn-dark btn-md">
                                    <i class="fas fa-edit"></i> Редактирай
                                </a>
                            </c:if>
                            <a class="btn btn-dark btn-md" href="/forum/thread/${thread.getID()}/reply/new" title="reply">
                                <i class="fas fa-reply"></i> Отговори
                            </a>
                        </div>
                        <br />
                        <c:forEach items="${replies}" var="reply">
                            <div class="card w-100 my-2">
                                <c:if test="${not empty reply.getIssuerName()}">
                                    <div class="card-header">
                                        <p><b>Отговор от:</b> ${reply.getIssuerName()}</p>
                                    </div>
                                </c:if>
                                <div class="card-body">
                                    <c:out value="${reply.getBody()}" escapeXml="false" />
                                </div>
                                <c:if test="${not empty reply.getDateTime()}">
                                    <div class="card-footer text-muted">
                                        <p><b>Отговорено на:</b> ${reply.getDateTime()}</p>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
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
