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
<title>Documents</title>
</head>

<body>
	<div class="panel">
		<%@include file="boxes/nav.jsp"%>
		<section class="page">
			<header class="header">
				<%@include file="boxes/header.jsp"%>
			</header>
			<section class="content-doc-file">
				<header class="py-3">
					<h1 class="ty-page-title">
						<c:if test='${type == "personal"}'>Personal Documents</c:if>
						<c:if test='${type == "public"}'>Public Documents</c:if>
						<c:if test='${type == "search"}'>Search</c:if>
					</h1>

				</header>
				<div class="container text-center">
					<div class="row my-3">

						<div class="form-inline md-form mr-auto">
							<input id="searchTerm" class="form-control mr-sm-2" type="text"
								placeholder="Search documents..." aria-label="Search document">
							<button id="search" class="btn btn-dark btn-rounded btn-sm my-0">Search</button>
						</div>
						<form enctype="multipart/form-data" method="POST" action="/document/public/upload">
							<button type="submit" id="upload" class="btn btn-dark all-doc-btn">Upload</button>
							<input type="file" id="uploadFile" name="file">
						</form>
					</div>

					<c:forEach items="${documents}" var="document">
						<div class="doc-container row row-cols-auto py-2">
							<div class="col">
								<div class="form-check">
									<input class="form-check-input" type="checkbox" value=""
										id="defaultCheck1"> <label class="form-check-label"
										for="defaultCheck1"> ${document.getName()} </label>
								</div>
							</div>

							<div class="col-2">
								<button id="${document.getId()}"
									class="btn btn-dark download py-1">Download</button>
							</div>
						</div>
					</c:forEach>

				</div>
			</section>
			<nav aria-label="Page navigation example">
				<ul class="pagination justify-content-center">
					<li class="page-item"><a class="page-link" href="#"
						aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
							<span class="sr-only">Previous</span>
					</a></li>
					<li class="page-item"><a class="page-link" href="#">1</a></li>
					<li class="page-item"><a class="page-link" href="#">2</a></li>
					<li class="page-item"><a class="page-link" href="#">3</a></li>
					<li class="page-item"><a class="page-link" href="#"
						aria-label="Next"> <span aria-hidden="true">&raquo;</span> <span
							class="sr-only">Next</span>
					</a></li>
				</ul>
			</nav>
			<footer>
				<%@include file="boxes/footer.jsp"%>
			</footer>
		</section>

	</div>
	<script type="module" src="/js/documents.js"></script>
	<script type="module" src="/js/main.js"></script>
</body>

</html>