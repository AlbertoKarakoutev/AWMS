<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page import="java.util.Map" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <link href="/css/main.css" rel="stylesheet">

    <!--Add vendor's js files-->
    <script src="/webjars/jquery/3.5.1/jquery.js"></script>
    <script src="/webjars/bootstrap/4.5.3/js/bootstrap.bundle.js"></script>
    <script src="/webjars/font-awesome/5.15.1/js/fontawesome.js"></script>
    <sec:authentication property="principal.authorities" var="role"/>
    <c:if test="${role == '[employee]'}">
   	<title>Request a Leave</title>
    </c:if>
    <c:if test="${role != '[ADMIN]'}">
   	<title>${name}'s leaves</title>
    </c:if>
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
                    <h1 class="ty-page-title">Leave<c:if test="${name!=null}">s for ${name}</c:if></h1>
                </header>
	                <c:if test="${name==null}">
		                <div class="my-3">
		                    <form action="/employee/requestLeave">
		                        <div class="form-group">
		                        	<div class="md-form">
		  					<input placeholder="Start date..." type="date" name="startDate" id="startDate" class="form-control datepicker" required>
							<small id="startDate" class="form-text text-muted">YYYY-MM-DD</small>
						</div>
		                        </div>
		                        <div class="form-group">
		                        	<div class="md-form">
		  					<input placeholder="End date..." type="date" name="endDate" id="endDate" class="form-control datepicker" required>
		  					<small id="endDate" class="form-text text-muted">YYYY-MM-DD</small>
						</div>
		                        </div>
		                        <div class="form-group">
			                        <select class="form-control" name="paidStr" id="paid">
			                        	<option value="true">Paid</option>
			                        	<option value="false" selected>Unpaid</option>
			                        </select>
			                        <small id="paid" class="form-text text-muted">Type</small>
			                    </div>
		                        <button type="submit" class="btn btn-dark">Request</button>
		                    </form>
		                </div>
					</c:if>
			            	
                <c:if test="${leaves.size()>0}">
			            	<div class='modal-header'>
						<h5 class="modal-title">Upcoming</h5>
				    	</div>
				    	<div class="modal-body">
						<div class="table-responsive text-center">
			                    		<table class="table table-light">
			                        		<thead>
			                            			<tr>
				                                		<th scope="col">Start Date</th>
				                                		<th scope="col">End Date</th>
				                                		<th scope="col">Paid</th>
				                                		<c:if test="${name!=null}"><th scope="col"></th></c:if>
			                            			</tr>
			                            		</thead>
			                        		<tbody>
	                						<%List<Map<String, Object>> leaves = (List<Map<String, Object>>)request.getAttribute("leaves");
	                						for(Map<String, Object> leave : leaves){
	                        					LocalDate start = LocalDate.ofInstant(((Date) leave.get("start")).toInstant(), ZoneId.systemDefault());
												LocalDate end = LocalDate.ofInstant(((Date) leave.get("end")).toInstant(), ZoneId.systemDefault());
												if(start.isAfter(LocalDate.now())||start.isEqual(LocalDate.now())){%>	    				
								    				<tr>
						                            	<th scope="row">
														    <h4><%=start.toString()%></h4>
														</th>
														<th scope="row">
														    <h4><%=end.toString()%></h4>
														</th>
						                                <td>
						                                <%if((boolean)leave.get("paid")){%>
						                                	<h4>Yes</h4>
						                                <%}else{%>
						                                	<h4>No</h4>
						                                <%}%>
						                                </td>
						                                <c:if test="${name!=null}">
											<c:if test="${role == '[ADMIN]'}">
								                                <th scope="row">
													<a class="btn btn-dark btn-sm" href="/admin/employee/deleteLeave/?employeeID=${employeeID}&leave=<%=leaves.indexOf(leave)%>">Delete</a>
												</th>
											</c:if>
						                                </c:if>
						                           	</tr>
				                        		<%}
				                        	}%>	        		
								    	</tbody>
			              		    </table>
								</div>
			            	</div>
			            	<div class='modal-header'>
						 		<h5 class="modal-title">Past</h5>
				    	    </div>
				    		<div class="modal-body">
								<div class="table-responsive text-center">
			                    	<table class="table table-secondary">
			                        	<thead>
			                            	<tr>
				                                <th scope="col">Start Date</th>
				                                <th scope="col">End Date</th>
				                                <th scope="col">Paid</th>
			                            	</tr>
			                            </thead>
			                        	<tbody>
			                        	<%for(Map<String, Object> leave : leaves){
	                        					LocalDate start = LocalDate.ofInstant(((Date) leave.get("start")).toInstant(), ZoneId.systemDefault());
												LocalDate end = LocalDate.ofInstant(((Date) leave.get("end")).toInstant(), ZoneId.systemDefault());
												if(start.isBefore(LocalDate.now())){%>	    				
								    				<tr>
						                            	<th scope="row">
														    <h4><%=start.toString()%></h4>
														</th>
														<th scope="row">
														    <h4><%=end.toString()%></h4>
														</th>
						                                <td>
						                                <%if((boolean)leave.get("paid")){%>
						                                	<h4>Yes</h4>
						                                <%}else{%>
						                                	<h4>No</h4><%}%>
						                                </td>
						                           	</tr>
				                        		<%}
				                        	}%>	        		
								    	</tbody>
			              		    </table>
								</div>
			            	</div>
		            </c:if>	
                	<c:if test="${leaves.size()<=0}">
                		<h5 class="modal-title">No leaves to display!</h5>
                	</c:if>
            <footer>
                <%@include file="../../boxes/footer.jsp" %>
            </footer>
        </section>
    </section>
</div>
<script type="module" src="/js/main.js"></script>
</body>
</html>
