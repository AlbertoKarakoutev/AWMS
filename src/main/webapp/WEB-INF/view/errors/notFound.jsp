<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>

<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="UTF-8">
<link href="/css/main.css" rel="stylesheet">

<title>Not found!</title>
</head>
<body>
	<div class="panel">
		<section class="page">
			<header class="header">
				<%@include file="../boxes/header.jsp"%>
			</header>
			<section class="content">
				<div class="p-4">
					<header class="pb-5 mb-5">
						<h1 class="ty-page-title">ERROR</h1>
					</header>
					<div class="container-fluid text-center">
						<h1 class="pt-5 mt-5"><b>Status 403</b>						</h1>
						<h3 class="pb-5 mb-5">Not found!</h3>
						<div class="pt-5 mt-5">
							<a href="/" class="btn btn-dark btn-lg">Go back!</a>
						</div>
					</div>
				</div>
			</section>
			<footer>
				<%@include file="../boxes/footer.jsp"%>
			</footer>
		</section>
	</div>
</body>

</html>

