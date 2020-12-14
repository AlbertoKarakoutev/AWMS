<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
    uri="http://www.springframework.org/security/tags"%>
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
					<div class="d-flex my-3">
						<div class="flex-grow-1">
							<form class="form-inline" method='get' action='/document/public/search/'>
								<input class="form-control" type="text" name='name'
									placeholder="Search documents..." aria-label="Search documents...">
								<button class="btn btn-dark ml-2" type="submit">Search</button>
							</form>
						</div>
						<div>
							<form enctype="multipart/form-data" method="POST" action="/document/public/upload">
							    <button type="submit" id="upload" class="btn btn-dark">Upload</button>
							    <input type="file" id="uploadFile" name="file">
						    </form>
						</div>
					</div>
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
                                <th scope="row">${loop.index}</th>
                                <td>
						            <h4>${document.getName()}</h4>
						        </td>
                                <td>
								   <sec:authentication property="principal.authorities" var="role" />
								   <c:if test="${(document.getOwnerID() == employeeID)||(role == '[ADMIN]')}">
                                     <form action="/document/public/delete/${document.getId()}" method="POST">
									     <button class="btn btn-dark">Delete</button>
									 </form>
								   </c:if>
								</td>
                                <td>
							       <button id="${document.getId()}" class="btn btn-dark download">Download</button>
								</td>
                            </tr>
				    	</c:forEach>
                        </tbody>
					</table>
				</div>
				<nav aria-label="Page navigation example">
				    <ul class="pagination justify-content-center">
  				    	<li class="page-item">
						    <a class="page-link" href="#" title='previous page'>
						        <span aria-hidden="true">&laquo;</span>
                                <span class="sr-only">Previous</span>
							</a>
						</li>
    					<li class="page-item"><a class="page-link" href="#">1</a></li>
    					<li class="page-item"><a class="page-link" href="#">2</a></li>
    					<li class="page-item"><a class="page-link" href="#">3</a></li>
   						<li class="page-item">
						   <a class="page-link" href="#" title='next page'>
						        <span aria-hidden="true">&raquo;</span>
                                <span class="sr-only">Next</span>
						   </a>
						</li>
  					</ul>
				</nav>
			    <footer>
			    	<%@include file="boxes/footer.jsp"%>
			   </footer>
			</section>
		</section>

	</div>
	<script type="module" src="/js/documents.js"></script>
	<script type="module" src="/js/main.js"></script>
</body>

</html>