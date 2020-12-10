<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@page import="java.util.List"%> 
<%@page import="com.company.awms.data.employees.EmployeeDailyReference"%>
<%@page import="com.company.awms.data.schedule.Task"%>  
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
    <link href="/css/components/schedule.css" rel="stylesheet">
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
                <div class="working-shedule">Schedule</div>
                <div class="parent">
                    <div class="monday">Monday</div>
                    <div class="tuesday">Tuesday</div>
                    <div class="wednesday">Wednesday</div>
                    <div class="thursday">Thursday</div>
                    <div class="friday">Friday</div>
                    <div class="saturday">Saturday</div>
                    <div class="sunday">Sunday</div>
                    
                  <%List<EmployeeDailyReference>[] sle = (List<EmployeeDailyReference>[])request.getAttribute("sameLevelEmployees");
                   	List<Task>[] tasks = (List<Task>[])request.getAttribute("tasks");
                  	LocalDate thisMonth = LocalDate.now().withMonth((int)request.getAttribute("month"));
                    int offset = thisMonth.withDayOfMonth(1).getDayOfWeek().getValue()-1;
                   	for(int i = 0; i <= 34; i++){  
                   		if(i>=offset && i <= thisMonth.lengthOfMonth()){%>
                   	
		                    <button class='day-box'></button>				
		                    <div class='modal'>
		                    	<div class='modal-content'>
		                    		<span class='close'>&times;</span>
		                    		<div class='work-shifts'>
		                    			<p class="title">Employees</p>
				                    	  	<%
				                    	  		if(sle[i-offset] != null){
				                    	  			                    	  			                    			for(int j = 0; j < sle[i-offset].size(); j++){
				                    	  			                    	  				                    			out.println(sle[i-offset].get(j).getFirstName() + " " + sle[i-offset].get(j).getLastName() + " " + sle[i-offset].get(j).getWorkTimeInfo());
				                    	  	%>
					                    	</br>
			                    	
			                    
		                    	      			<%}
		                    	      		}%>
		                    		</div>
		                      		  
		                      	   	<%if(tasks[i-1] != null){%>
		                    			<div class="tasks">
			                    			<p class="title">Tasks</p>
			                    			<c:set var="day" value="init"/>
			                    		   	<%pageContext.setAttribute("day", i-1);%>
			                    			<c:forEach items="${tasks[day]}" var="task">
												<c:out value="${task.getTaskTitle()}"/>
											</c:forEach>
		                    			</div>
		                      	  	<%}%>
		                    	 	
		                    	</div>
		                    </div>
					
					  <%}else{%>
						
							<div class="empty-day"></div>		
						
					  <%}
                    }%>
                    
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
