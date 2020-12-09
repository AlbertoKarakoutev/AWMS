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
                        <h1 class="ty-page-title font-italic">Contacts</h1>
                    </header>
                    <div class="container">
                    <div class="row">
                        <div class="col-md-4">
                        <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                            </div>
                        </div>
                        <div class="col-md-4">
                           <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                            </div>
                        </div>
                        <div class="col-md-4">
                          <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                            </div>
                        </div>
                        <div class="col-md-4">
                          <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                          </div>
                        </div>
                        <div class="col-md-4">
                          <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                          </div>
                        </div>
                        <div class="col-md-4">
                          <div class="card-body">
                            <div class="border rounded p-4 h-100">
                                <div class="name-contact">
                                    <i class="fas fa-signature"> Name:</i>
                                </div>
                                <div class="work-position-contact">
                                    <i class="fas fa-briefcase"> Work position:</i>
                                </div>
                                <div class="telephone-num">
                                    <i class="fas fa-phone-alt"> Telephone number:</i>
                                </div>
                            </div>
                            </div>
                        </div>

                    </div>
                </div>
                </div>
            </section>


            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
        </section>
    </div>
    <script type="module" src="/js/main.js"></script>
</body>

</html>