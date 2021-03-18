<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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
    <title>Forum</title>
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
                    <h1 class="ty-page-title">Forum</h1>
                </header>
                <div class="d-flex">
	                <form method='GET' action='/forum/search' class='mr-auto px-2 pd-0 md-0'>
				<div class="form-row">
					<div class="form-group col-lg">
						<input class="form-control" type="text" name='searchTerm'
						 id="forumSearch" placeholder="Search..." aria-label="Search..." required>
					</div>
					<div class="form-group col-md-4">
						<button class="btn btn-dark" type="submit">Search</button>
					</div>
				</div>
			</form>
	                <div class="ml-auto py-0">
	                       <a class="btn btn-dark" href="/forum/thread/new" title="New thread">New question</a>
	                 </div>
                </div>
                <div class="md-3">
                <c:forEach items="${threads}" var="thread">
                	<div class="d-flex my-3">
	                	<a class="forum-questions text-left text-light btn btn-lg mr-auto btn-dark" href="/forum/thread/${thread.getID()}" title="${thread.getTitle()}">
		                        ${thread.getTitle()} <c:if test="${thread.getAnswered() == true}"> (Answered) </c:if><small class="font-italic">by ${thread.getIssuerName()}</small>
		                        
		                </a>
		                <c:if test="${role == '[ADMIN]'}">            
		                        <a class="btn btn-lg btn-danger ml-1" href="/admin/forum/delete/${thread.getID()}">
		                        	Delete
		                       	</a>
	                       	</c:if>
                       	</div>
                </c:forEach>
                </div>
                </div>
                	<c:if test='${!path.equals("search")}'>
			<nav>
				<ul class="pagination justify-content-center">
					<c:if test='${Integer.parseInt(page) > 1}'>
						<li class="page-item"><a class="page-link" href="/forum${path}/?page=1"> <span
								aria-hidden="true">&laquo;</span> <span class="sr-only"></span></a>
						</li>
						<li class="page-item"><a class="page-link" href="/forum${path}/?page=${page-1}">${page-1}</a></li>
					</c:if>
					
					<li class="page-item active"><a class="page-link" href="#">${page}</a></li>
					
					<c:if test='${Integer.parseInt(page) < pageCount}'>
						<li class="page-item"><a class="page-link" href="/forum${path}/?page=${page+1}">${page+1}</a></li>
						<li class="page-item"><a class="page-link" href="/forum${path}/?page=${pageCount}"> <span
							aria-hidden="true">&raquo;</span> <span class="sr-only"></span></a>
						</li>
					</c:if>
				</ul>
			</nav>
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
