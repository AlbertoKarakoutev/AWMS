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
<title>Dashboard</title>
</head>
<body>
	<div class="panel">
		<%@include file="boxes/nav.jsp"%>
		<section class="page">
			<header class="header">
				<%@include file="boxes/header.jsp"%>
			</header>
			<section class="content">
                <div class="p-4">
			    	<header class="py-3">
                        <h1 class="ty-page-title">Employees</h1>
                    </header>
                    <div class="flex-grow-1">
						<form class="form-inline" method='get' action='/admin/employee/search'>
							<input class="form-control" type="text" name='searchTerm'
								placeholder="Search employees..." aria-label="Search mployees...">
								<div class="form-group">
                            	<label for="departmentEmployee">Search by: </label>
                            	<select class="form-control" name="type" id="departmentEmployee">
                                    <option selected="NATIONAL ID">NATIONAL ID</option>
                                    <option selected="FIRST NAME">FIRST NAME</option>
                                    <option selected="LAST NAME">LAST NAME</option>
                                    <option selected="ROLE">ROLE</option>
                                    <option selected="E-MAIL">E-MAIL</option>
                                    <option selected="IBAN">IBAN</option>
                                    <option selected="LEVEL">LEVEL</option>
                                    <option selected="DEPARTMENT">DEPARTMENT</option>
                                    <option selected="ACCESS LEVEL">ACCESS LEVEL</option>
                                
                            	</select>
                           </div>
							<button class="btn btn-dark ml-2" type="submit">Search</button>
						</form>
					</div>
				    <table class="table">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">First Name</th>
                                <th scope="col">Family Name</th>
                                <th scope="col">Department</th>
								<th scope="col">Edit</th>
                            </tr>
                        </thead>
                        <tbody>
                        
                            <c:forEach items="${employees}" var="employee" varStatus="loop">
							    <tr>
                                <th scope="row">${loop.index}</th>
                                <td>
						            <h4>${employee.getFirstName()}</h4>
						        </td>
                                <td>
								    <h4>${employee.getLastName()}</h4>
								</td>
                                <td>
							        <h4>${departments.get(employee.getDepartment())}</h4>
								</td>
								<td>
								    <a class="btn btn-dark" href="/admin/employee/edit/${employee.getID()}" title="Edit employee ${employee.getFirstName()}">
									    Edit
									</a>
								</td>
                                </tr>
			                </c:forEach>
					    </tbody>
					</table>
				</div>
			    <footer>
			    	<%@include file="boxes/footer.jsp"%>
		    	</footer>
		</section>
	</div>
	<script type="module" src="/js/main.js"></script>
</body>
</html>
