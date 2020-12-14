<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@	page import="java.util.*" %>
<%@	page import="org.json.simple.JSONArray"%>
<%@	page import="org.json.simple.JSONObject"%>
<%@	page import="org.json.simple.parser.JSONParser"%>
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
	<title>Dashboard</title>
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
	                    <h1 class="ty-page-title">Department Editor</h1>
	                </header>
                    <div class="form-group">
	                <label for="departments">Select Department:</label>
	                <select id="departments" class="form-control">
	                <%Map<String, String> departments = (Map<String, String>)request.getAttribute("departments");
	                System.out.println(departments.get("a"));
	                if(departments!=null){
	                	for(Map.Entry<String, String> department : departments.entrySet()){%>
	                			<option value='<%=department.getKey()%>'> <%=departments.get(department.getKey())%> </option>
	                	<%}
	                }%>
                	</select>
                    </div>
                	<div id="button-content">
                		<button id="display" class="btn btn-dark m-1">DISPLAY</button>
                		<button id="add" class="btn btn-dark m-1">ADD NEW</button>
                		<button id="addLevel" class="btn btn-dark m-1" style="{display:none;}">ADD LEVEL</button>
                		<button id="create" class="btn btn-danger m-1" style="{display:none;}">CREATE DEPARTMENT</button>
                		<button id="update" class="btn btn-danger m-1" style="{display:none;}">UPDATE</button>
                	</div>
                	
                	<div id="department-content" class="my-3"></div>
                </div>
                <footer>
                    <%@include file="boxes/footer.jsp" %>
                </footer>
            </section>
        </section>
    </div>
<script type="module" src="/js/departments.js"></script>
<script type="module" src="/js/main.js"></script>
</body>
</html>
