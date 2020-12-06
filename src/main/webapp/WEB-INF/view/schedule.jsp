<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@page import="java.util.List"%> 
<%@page import="com.company.awms.data.employees.EmployeeDailyReference"%> 
<%@page import="java.time.LocalDate"%> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script
        src="https://code.jquery.com/jquery-3.5.1.min.js"
        integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
        crossorigin="anonymous">
    </script>  
	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <link href="/css/main.css" rel="stylesheet">
    <link href="/css/components/calendar.css" rel="stylesheet">
    <script src="/js/main.js"></script>

	<title>Calendar</title>
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
                <div class="working-shedule">Working Schedule</div>
                <div class="parent">
                    <div class="monday">Monday</div>
                    <div class="tuesday">Tuesday</div>
                    <div class="wednesday">Wednesday</div>
                    <div class="thursday">Thursday</div>
                    <div class="friday">Friday</div>
                    <div class="saturday">Saturday</div>
                    <div class="sunday">Sunday</div>
                    
                    <%
                   	List<EmployeeDailyReference>[] sle = (List<EmployeeDailyReference>[])request.getAttribute("sameLevelEmployees");
                   	int firstWeekday = LocalDate.now().withDayOfMonth(1).getDayOfWeek().getValue();
                   	for(int i = 0; i <= 34; i++){  
                   	%>
                   	
                    <button class='day-box'></button>
                    	<div class='modal'>
                    		<div class='modal-content'>
                    			<span class='close'>&times;</span>
                    			<p class='info'>
                    			
                    			<% 
                    			if(i>=firstWeekday-1 && i <=LocalDate.now().lengthOfMonth()-2){
                    				if(sle[i] != null){
	                    				for(int j = 0; j < sle[i].size(); j++){
	                    					
	                    					out.println(sle[i].get(j).getRefFirstName() + " " + sle[i].get(j).getRefLastName() + " " + sle[i].get(j).getWorkTimeInfo());
	                    		%>
	                    		</br>
	                    		<%       		
	                    					
	                    				}
                    				}
                    			}
                    			%>
                    			
                    			</p>
                    		</div>
                    	</div>
					
					<%
                    }
                    %>
                    
                </div>
            </section>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
    </div>
	
    <script src="/js/schedule.js"></script>
</body>
</html>
