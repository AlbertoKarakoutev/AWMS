<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@	taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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
	<sec:authentication property="principal.authorities" var="role" />
	<div class="panel">
		<%@include file="../../boxes/nav.jsp"%>
		<section class="page">
			<header class="header">
				<%@include file="../../boxes/header.jsp"%>
			</header>
			<section class="content">
				<div class="p-4">
					<header class="py-3">
						<h1 class="ty-page-title">Employees</h1>
					</header>
					<div class="my-3 d-flex">
						<form method='get' action='/admin/employee/search' class='mr-auto p-2'>
							<div class="form-row">
								<div class="form-group col-md-4">
									<input class="form-control" type="text" name='searchTerm' id="employeesSearch"
										placeholder="Search employees..." aria-label="Search employees...">
								</div>
								<div class="form-group col-md-4">
									<select class="form-control" name="type" id="departmentEmployee">
										<option>NATIONAL ID</option>
										<option selected>FIRST NAME</option>
										<option>LAST NAME</option>
										<option>ROLE</option>
										<option>E-MAIL</option>
										<option>IBAN</option>
										<option>LEVEL</option>
										<option>DEPARTMENT CODE</option>
										<option>ACCESS LEVEL</option>
									</select>
								</div>
								<div class="form-group col-md-2">
									<button class="btn btn-dark" type="submit">Search</button>
								</div>
							</div>
						</form>

						<c:if test="${role == '[ADMIN]'}">
							<div class="w-50 p-2">
								<a class="btn btn-danger" href="/admin/schedule/apply" title="Apply schedule">Calculate
									schedule</a>
							</div>
							<div class="p-2">
								<a class="btn btn-dark" href="/admin/employee/register" title="Add employee">Add</a>
							</div>
						</c:if>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th scope="col">#</th>
									<th scope="col">First Name</th>
									<th scope="col">Family Name</th>

									<c:if test="${role == '[ADMIN]'}">
										<th scope="col">Role</th>
										<th scope="col">Department</th>
										<th scope="col">Edit</th>
									</c:if>
									<th scope="col">Leaves</th>
									<c:if test="${role == '[ADMIN]'}">
										<th scope="col">Delete</th>
									</c:if>
								</tr>
							</thead>
							<tbody>

								<c:forEach items="${employees}" var="employee" varStatus="loop">
									<tr>
										<c:if test='${type == "all"}'>
											<th scope="row">${(page-1)*10 + loop.index + 1}</th>
										</c:if>
										<c:if test='${type != "all"}'>
											<th scope="row">${loop.index + 1}</th>
										</c:if>
										<td>
											<h4>${employee.getFirstName()}</h4>
										</td>
										<td>
											<h4>${employee.getLastName()}</h4>
										</td>

										<c:if test="${role == '[ADMIN]'}">
											<td>
												<h4>${employee.getRole()}</h4>
											</td>
											<td>
												<h4>${departments.get(employee.getDepartment())}</h4>
											</td>
											<td><a class="btn btn-dark" href="/admin/employee/edit/${employee.getID()}"
												title="Edit employee ${employee.getFirstName()}"> Edit </a></td>
											<td><a class="btn btn-dark" href="/admin/employee/leaves/${employee.getID()}"
												title="Edit employee ${employee.getFirstName()}"> View Leaves </a></td>
											<td><a class="btn btn-danger"
												href="/admin/employee/delete/?employeeID=${employee.getID()}">Delete</a></td>
										</c:if>
										<c:if test="${role == '[MANAGER]'}">
											<td><a class="btn btn-dark" href="/employee/manager/leaves/${employee.getID()}"
												title="Edit employee ${employee.getFirstName()}"> View Leaves </a></td>
										</c:if>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<c:if test='${type.equals("all")}'>
					<nav>
						<ul class="pagination justify-content-center">
							<c:if test='${Integer.parseInt(page) > 1}'>
								<li class="page-item"><a class="page-link" href="${link}page=1"> <span
										aria-hidden="true">&laquo;</span> <span class="sr-only"></span></a></li>
								<li class="page-item"><a class="page-link" href="${link}page=${page-1}">${page-1}</a></li>
							</c:if>

							<li class="page-item active"><a class="page-link" href="#">${page}</a></li>

							<c:if test='${Integer.parseInt(page) < pageCount}'>
								<li class="page-item"><a class="page-link" href="${link}page=${page+1}">${page+1}</a></li>
								<li class="page-item"><a class="page-link" href="${link}page=${pageCount}">
										<span aria-hidden="true">&raquo;</span> <span class="sr-only"></span>
								</a></li>
							</c:if>
						</ul>
					</nav>
				</c:if>
				<footer>
					<%@include file="../../boxes/footer.jsp"%>
				</footer>
			</section>
		</section>
	</div>
	<script type="module" src="/js/main.js"></script>
</body>
</html>
