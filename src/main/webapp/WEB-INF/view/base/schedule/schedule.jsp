<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.company.awms.modules.base.employees.data.EmployeeDailyReference"%>
<%@page import="com.company.awms.modules.base.schedule.data.Task"%>
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
	<%
	boolean[] sle = (boolean[]) request.getAttribute("employeeWorkDays");
	List<Task>[] tasks = (List<Task>[]) request.getAttribute("tasks");
	YearMonth yearMonth = (YearMonth) request.getAttribute("month");
	LocalDate thisMonth = LocalDate.now().withYear(yearMonth.getYear()).withMonth(yearMonth.getMonthValue());
	int offset = thisMonth.withDayOfMonth(1).getDayOfWeek().getValue() - 1;
	%>

	<div class="panel">
		<nav class="navigation">
			<%@include file="../../boxes/nav.jsp"%>
		</nav>
		<section class="page">
			<header class="header">
				<%@include file="../../boxes/header.jsp"%>
			</header>
			<section class="content">
				<div id="mainBody" class="p-4">
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
						<div id='swap-modal' class='modal fade' tabindex='-1' role='dialog' aria-hidden='true'>
							<div class='modal-dialog modal-dialog-centered modal-xl' role='document'>
								<div class='modal-content'>
									<div class='modal-header'>
										<h5 class='modal-title'>Swap Shifts</h5>
										<button type='button' class='close' data-dismiss='modal' aria-label='Close'>
											<span aria-hidden='true'><i class='fas fa-times'></i></span>
										</button>
									</div>
									<div class='modal-body'>
										<form method="GET" action="/schedule/swapRequest">
											<div class="form-group">
												<div class="md-form">
													<select name="requesterDate" id="dates" class="form-control">

													</select>
												</div>
											</div>
											<small class='form-text text-muted'>Select the date, which you wish to switch.</small><br>
											<input type="hidden" id="receiverNationalID" name="receiverNationalID"> <input
												type="hidden" id="receiverDate" name="receiverDate">
										</form>
										<button class="btn btn-dark" id="swap-request-btn" data-dismiss="modal" onclick='sendSwapRequest()'>Send
											request!</button>
									</div>
									<div class='modal-footer'></div>
								</div>
							</div>
						</div>
						<div class='modal fade' id="employeeModal" tabindex="-1" role="dialog"
							aria-labelledby="EmployeeModal" aria-hidden="true">
							<div class='modal-dialog modal-dialog-centered modal-xl' role="document">
								<div class='modal-content'>
									<div class='modal-header'>
										<h5 id='EmployeeModal' class="modal-title">Employees</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true"> <i class="fas fa-times"></i>
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
												<tbody id="day-modal-body">
													<tr></tr>
												</tbody>
											</table>
										</div>
										<table id="task-table" class="table">
											<thead>
												<tr>
													<th scope="col">Title</th>
													<th scope="col">Assignment</th>
													<th scope="col">Reward</th>
													<th scope="col" style="text-align:center">State</th>
												</tr>
											</thead>
											<tbody>
												<tbody id="task-modal-body">
													<tr></tr>
												</tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
						<div class="mt-2">
							<div class="row seven-cols">
								<%for (int i = 0; i <= 34; i++) {%>
								<div class="col-md-1 p-0">
									<%if (i >= offset && i < thisMonth.lengthOfMonth() + offset) {%>
										<button class='day-box' onclick='getDayData(<%=i-offset+1%>)' data-toggle="modal" data-target="#employeeModal">
											<%if(sle[i - offset + 1]){%>
												<span class='work-day'></span>
											<%} %>
										</button>
									<%}else{%>
										<div class="empty-day"></div>
									<%}%>
								</div>
								<%} %>
							</div>
						</div>
					</div>
				</div>
				<div class="my-2 text-center">
					<%if (yearMonth.equals(YearMonth.now())) {%>
						<a class="btn btn-dark btn-lg" href="/schedule/?month=<%=YearMonth.now().plusMonths(1)%>">
							<i class="fas fa-chevron-right"></i>
						</a>
					<%} else {%>
						<a class="btn btn-dark btn-lg" href="/schedule/?month=<%=YearMonth.now()%>"> <i class="fas fa-chevron-left"></i>
						</a>
					<%}%>
				</div>
				<footer>
					<%@include file="../../boxes/footer.jsp"%>
				</footer>
			</section>
		</section>
	</div>
	<script src="/js/main.js"></script>
	<script src="/js/schedule.js"></script>
</body>
</html>
