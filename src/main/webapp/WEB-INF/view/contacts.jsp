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
    <title>Contacts</title>
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
                        <h1 class="ty-page-title">Contacts</h1>
                    </header>
                    <div class="container-fluid">
                        <div class="row">
                            <div class="visit-card col-md-4 card-body">
                                <div class="border rounded p-4">
                                    <div class="d-flex align-items-center">
                                        <i class="fas fa-signature mr-1"></i> 
                                        <p>
                                            <b>Name:</b> 
                                            ${owner.getFirstName()} ${owner.getLastName()}
                                        </p>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <i class="fas fa-briefcase mr-1"></i> 
                                        <p>
                                            <b>Work position:</b> 
                                            ${owner.getRole()}
                                        </p>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <i class="fas fa-phone-alt mr-1"></i> 
                                        <p>
                                            <b>Telephone number:</b> 
                                            ${owner.getPhoneNumber()}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        
                            <c:forEach items="${managers}" var="manager">
                                <div class="visit-card col-md-4 card-body">
                                    <div class="border rounded p-4">
                                        <div class="d-flex align-items-center">
                                            <i class="fas fa-signature mr-1"></i> 
                                            <p>
                                                <b>Name:</b> 
                                                ${manager.getFirstName()} ${manager.getLastName()}
                                            </p>
                                        </div>
                                        <div class="d-flex align-items-center">
                                            <i class="fas fa-briefcase mr-1"></i> 
                                            <p> 
                                                <b>Work position:</b>
                                                ${manager.getRole()}
                                            </p>
                                        </div>
                                        <div class="d-flex align-items-center">
                                            <i class="fas fa-phone-alt mr-1"></i> 
                                            <p>
                                                <b>Telephone number: </b>
                                                ${manager.getPhoneNumber()}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </section>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
    </div>
    <script type="module" src="/js/main.js"></script>
</body>

</html>
