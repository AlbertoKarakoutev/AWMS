<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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
<title>Documents</title>
</head>

<body>
	<div class="panel">
		<%@include file="../../boxes/nav.jsp"%>
		<section class="page">
			<header class="header">
				<%@include file="../../boxes/header.jsp"%>
			</header>

			<section class="content">
				<div class="p-4">
					<header class="py-3">
						<h1 class="ty-page-title">
							<c:if test='${type == "personal"}'>Personal Documents</c:if>
							<c:if test='${type == "public"}'>Public Documents</c:if>
							<c:if test='${type == "search"}'>Search</c:if>
							<c:if test='${type == "admin-edit"}'>Editing ${name}'s personal documents:</c:if>
						</h1>
					</header>
					<div class="d-md-flex my-3">
						<c:if test='${type == "public" || type == "search"}'>
							<div class="flex-grow-1 my-2">
								<form class="form-inline" method='get' action='/document/public/search/'>
									<div class="form-group m-0">
										<input class="form-control" type="text" name='name' placeholder="Search document"
											aria-label="Search documents..." required>
									</div>
									<button class="btn btn-dark ml-2" type="submit">Search</button>
								</form>
							</div>
							<div class="my-2 my-md-0">
								<form class="form-inline" enctype="multipart/form-data" method="POST"
									action="/document/public/upload">
									<div class="form-group m-0">
										<input type="file" id="uploadFile" name="file" required>
									</div>
									<div class="form-group">
							                        <label for="limitedAccess">Limited access:  </label>
							                        <select name="limitedAccess" class="form-control mx-2" id="limitedAccess">
							                        	<option value="false">No</option>
							                        	<option value="true">Yes</option>
							                        </select>
							                </div><button type="submit" id="upload" class="btn btn-dark m-2 ml-0">Upload</button>
									
								</form>
							</div>
						</c:if>
						<c:if test='${type == "admin-edit"}'>
							<div class="my-2 my-md-0">
								<form class="form-inline" enctype="multipart/form-data" method="POST"
									action="document/upload/${ownerID}">
									<button type="submit" id="upload-personal" class="btn btn-dark m-2 ml-0">Upload</button>
									<div class="form-group m-0">
										<input type="file" id="uploadPersonalFile" name="file" required>
									</div>
								</form>
							</div>
						</c:if>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th scope="col">#</th>
									<th scope="col">Name</th>
									<th scope="col"></th>
									<th scope="col">Download</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${documents}" var="document" varStatus="loop">
									<tr>
										<c:if test='${type == "public"}'>
											<th scope="row">${(page-1)*10 + loop.index + 1}</th>
										</c:if>
										<c:if test='${type != "public"}'>
											<th scope="row">${loop.index + 1}</th>
										</c:if>
										<td>
											<h4>${document.getName()}</h4>
										</td>
										<td><sec:authentication property="principal.authorities" var="role" /> <c:if
												test='${type == "public" || type == "search"}'>
												<form action="/document/public/delete/${document.getId()}" method="POST">
													<button class="btn btn-dark">Delete</button>
												</form>
											</c:if> <c:if test='${type == "admin-edit"}'>
												<form action="/admin/document/personal/delete/${document.getId()}/?ownerID=${ownerID}"
													method="POST">
													<button class="btn btn-dark">Delete</button>
												</form>
											</c:if> <c:if test='${type == "personal" && role == "[ADMIN]"}'>
												<form
													action="/admin/document/personal/delete/${document.getId()}/?ownerID=${employeeID}"
													method="POST">
													<button class="btn btn-dark">Delete</button>
												</form>
											</c:if></td>
										<td><c:if test='${type == "public" || type == "search"}'>
												<form action="/document/public/download/${document.getId()}" method="GET">
													<button class="btn btn-dark download">Download</button>
												</form>
											</c:if> <c:if test='${type == "personal" || type == "admin-edit"}'>
												<form action="/document/personal/download/${ownerID}/${document.getId()}" method="GET">
													<button class="btn btn-dark download">Download</button>
												</form>
											</c:if></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<c:if test='${type == "public"}'>
					<nav>
						<ul class="pagination justify-content-center">
							<c:if test='${Integer.parseInt(page) > 1}'>
								<li class="page-item"><a class="page-link" href="/document/public/?page=1"> <span
										aria-hidden="true">&laquo;</span> <span class="sr-only"></span></a>
								</li>
								<li class="page-item"><a class="page-link" href="/document/public/?page=${page-1}">${page-1}</a></li>
							</c:if>
							
							<li class="page-item active"><a class="page-link" href="#">${page}</a></li>
							
							<c:if test='${Integer.parseInt(page) < pageCount}'>
								<li class="page-item"><a class="page-link" href="/document/public/?page=${page+1}">${page+1}</a></li>
								<li class="page-item"><a class="page-link" href="/document/public/?page=${pageCount}"> <span
									aria-hidden="true">&raquo;</span> <span class="sr-only"></span></a>
								</li>
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
	<script type="module" src="/js/documents.js"></script>
	<script type="module" src="/js/main.js"></script>
</body>

</html>
