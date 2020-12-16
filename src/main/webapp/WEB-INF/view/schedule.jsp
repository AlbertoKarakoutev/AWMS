<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@page import="java.util.List"%> 
<%@page import="com.company.awms.data.employees.EmployeeDailyReference"%>
<%@page import="com.company.awms.data.schedule.Task"%>  
<%@page import="java.time.LocalDate"%> 
<%@	taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/main.css" rel="stylesheet">
	
    <!--Add vendor's js files-->
    <script src="/webjars/jquery/3.5.1/jquery.js"></script>
    <script src="/webjars/bootstrap/4.5.3/js/bootstrap.bundle.js"></script>
    <script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script> 

	<title>Calendar</title>
</head>
<body>
	<sec:authentication property="principal.authorities" var="role" />
	
	<%List<EmployeeDailyReference>[] sle = (List<EmployeeDailyReference>[])request.getAttribute("sameLevelEmployees");
	List<Task>[] tasks = (List<Task>[])request.getAttribute("tasks");
	YearMonth yearMonth = (YearMonth)request.getAttribute("month");
	LocalDate thisMonth = LocalDate.now().withYear(yearMonth.getYear()).withMonth(yearMonth.getMonthValue());
	int offset = thisMonth.withDayOfMonth(1).getDayOfWeek().getValue()-1; %>
    
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
                        <h1 class="ty-page-title"><%=yearMonth.getMonth()%> <%=yearMonth.getYear()%></h1>
                    </header>
				    <div class="container-fluid">
				    	<div class="row seven-cols">
				    		<div class="col-md-1">
				    			<h3 class="text-center">Monday</h3>
				    		</div>
				    		<div class="col-md-1">
			    				<h3 class="text-center">Tuesday</h3>
			    			</div>
	    					<div class="col-md-1">
	    						<h3 class="text-center">Wednesday</h3>
	    					</div>
	    					<div class="col-md-1">
		    					<h3 class="text-center">Thursday</h3>
		    				</div>
		    				<div class="col-md-1">
		    					<h3 class="text-center">Friday</h3>
		    				</div>
		    				<div class="col-md-1">
		    					<h3 class="text-center">Saturday</h3>
		    				</div>
		    				<div class="col-md-1">
		    					<h3 class="text-center">Sunday</h3>
		    				</div>
		    			</div>
						<div class="mt-2">
						    <div class="row seven-cols">
								<%for(int i = 0; i <= 34; i++){%>
								    <c:set var="i" value="<%=i%>" />

								    <div class="col-md-1 p-0">
                   						<%if(i>=offset && i <= thisMonth.lengthOfMonth()){%>
		             	     	 	        <button class='day-box' data-toggle="modal" data-target="#employeeModal${i}"></button>				
		                 	  		    	<div class='modal fade' id="employeeModal${i}" tabindex="-1" role="dialog" aria-labelledby="EmployeeModal${i}" aria-hidden="true">
		                         	  		    <div class='modal-dialog modal-dialog-centered modal-xl' role="document">
												    <div class='modal-content'>
												    	<div class='modal-header'>
												            <h5 id='EmployeeModal${i}' class="modal-title">Employees</h5>
												            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                <span aria-hidden="true">
												    	    	    <i class="fas fa-times"></i>
												    	    	</span>
                                                            </button>
											    	    </div>
											    		<div class="modal-body">
														<div class="table-responsive text-center">
				                                            <table class="table">
                                                                <thead>
                                                                    <tr>
                                                                        <th scope="col">Name</th>
                                                                        <th scope="col">Working Hours</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody id="body<%=i%>">
				                    	    		          	    <%String day = thisMonth.withDayOfMonth(i).toString();%>
				                    	    		          	    <%if(sle[i-offset+1] != null){%>
				                    	    		          	    	<%for(int j = 0; j < sle[i-offset+1].size(); j++){
				                     		 	  	          	    		EmployeeDailyReference thisEDR = sle[i-offset+1].get(j);%>
																    				<tr id="<%=i%>-<%=thisEDR.getNationalID()%>">
	                                                                                    <th scope="row">
																						    <h4><%= thisEDR.getFirstName() %> <%=thisEDR.getLastName() %></h4>
																						</th>
	                                                                                    <td>
									                                                        <h4><%= thisEDR.getWorkTimeInfo() %></h4>
									                                                    </td>
																						<c:if test="${role == '[EMPLOYEE]'}">
		                                                                                    <td>
									                                                            <button class="btn btn-dark" onclick='datePrompt("<%=thisEDR.getNationalID()%>", "<%=day%>")'>Swap Shifts</button>
										                                                    </td>
									                                                    </c:if>
									                                                    <c:if test="${role == '[ADMIN]'}">
									                                                    	<td>
									                                                            <button class="btn btn-dark" onclick='deleteWorkDay("<%=i%>","<%=thisEDR.getNationalID()%>", "<%=day%>")'>Delete</button>
										                                                    </td>
									                                                    </c:if>
									                                                    <c:if test="${role == '[MANAGER]'}">
									                                                    	<td>
									                                                            <button class="btn btn-dark" onclick='getTaskInput("<%=i%>", "<%=thisEDR.getNationalID()%>","<%=day%>")'>Add a Task</button>
										                                                    </td>
									                                                    </c:if>
									                                                <%}%>
									                                                <c:if test="${role == '[ADMIN]'}">
									                                                	<td>
			                                                    							<button class="btn btn-dark" onclick='getInput("<%=i%>", "<%=day%>")'>Add </button>
			                                                    						</td>
				                                                 					</c:if>
                                                                                	</tr>
		                    	          		        				<%}%>
		                    	          		        		
															    </tbody>
		                      		                        </table>
															</div>
		                          	  	    	         	<%if(tasks[i-1] != null){%>
		                    	    	 		        	   	<table class="table">
			                    	    	 		        	   	<thead>
	                                                                    <tr>
	                                                                        <th scope="col">Title</th>
	                                                                        <th scope="col">Assignment</th>
	                                                                        <th scope="col">Reward</th>
	                                                                        <th scope="col">State</th>
	                                                                    </tr>
	                                                                </thead>
	                                                                <tbody>
				                            		        			<c:set var="day" value="init"/>
				                        	    	        			<%for(int index = 0; index < tasks[i-offset].size();index++){%>
				                        	    	        				<tr>
				                        	    	        					<th scope="row">
																					<h4><b></b><c:out value="<%=tasks[i-offset].get(index).getTaskTitle()%>"/></h4>
																				</th>
																				<td>
									                                            	<h4><c:out value="<%=tasks[i-offset].get(index).getTaskBody()%>"/></h4>
									                                            </td>
									                                            <td>
									                                            	<h4><c:out value="<%=tasks[i-offset].get(index).getTaskReward()%>"/></h4>
									                                            </td>
									                                            <%if(!tasks[i-offset].get(index).getCompleted()){%>
										                                            <td>
										                                            	<a class="btn btn-dark" href="/schedule/taskComplete/?taskNum=<%=index%>&date=<%=day%>">Mark as Complete</a>
										    	    	        					</td>
										    	    	        				<%}else{%>
										    	    	        					<td>
										                                            	<i class="fas fa-check-circle"></i>
										    	    	        					</td>
										    	    	        				<%}%>
									    	    	        				</tr>
									    	    	        			<%}%>
								    	    	        			</tbody>
								    	    	        		</table>
		                          	  	            		<%}%> 	
												    	</div>
		                             			    </div>
											    </div>
		                           			</div>
				             			<%}else{%>
					    		    		<div class="empty-day"></div>		
					            		<%}%>
									</div>
                           		<%}%>
							</div>
				   	 	</div>
		    		</div>
				</div>
				<div class="my-2 text-center">
					<%if(yearMonth.equals(YearMonth.now())){%>
            			<a class="btn btn-dark btn-lg" href="/schedule/?month=<%=YearMonth.now().plusMonths(1)%>">
					    	<i class="fas fa-chevron-right"></i>
						</a>		
         		   	<%}else{%>
            			<a class="btn btn-dark btn-lg" href="/schedule/?month=<%=YearMonth.now()%>">
						    <i class="fas fa-chevron-left"></i>
						</a>
            		<%}%>
				</div>
                <footer>
                    <%@include file="boxes/footer.jsp" %>
                </footer>
            </section>
        </section>
    </div>
	
    <script src="/js/main.js"></script>
    <script src="/js/schedule.js"></script>
</body>
</html>
