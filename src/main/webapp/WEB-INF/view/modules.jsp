<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.*" %>
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
	<title>Employees</title>
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
                    <h1 class="ty-page-title">Active Modules</h1>
                </header>
                <div class="my-3 d-flex flex-row-reverse">
                    <button class="btn btn-dark module-update">Update</button>
                </div>
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col" class="col-11">Module Name</th>
                            <th scope="col" class="col-1">Active</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%Map<String, Boolean> modules = (Map<String, Boolean>)request.getAttribute("modules");
                        for(Map.Entry<String, Boolean> module : modules.entrySet()){ %>
                                <tr>
                                    <td class="col-11">
						                <h4 class="module-name"><%=module.getKey()%></h4>
					                </td>
                	                <td class="col-1">
                                        <div class="text-center">
                                            <c:set var="check" value="<%= (boolean)(modules.get(module.getKey())) %>" />
                                            <input type="checkbox" id="<%=module.getKey()%>" name="<%=module.getKey()%>" ${check ? "checked" : ""}>
                                        </div>
                                    </td>
                                </tr>
                            <%}%>
                        
                    </tbody>
                </table>
            </div>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
            </section>
        </section>
    </div>
<script type="module" src="/js/modules.js"></script>
</body>
</html>
