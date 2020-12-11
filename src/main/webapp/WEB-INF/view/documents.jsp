<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script
        src="https://code.jquery.com/jquery-3.5.1.min.js"
        integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0="
        crossorigin="anonymous">
    </script>  
	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <link href="/css/main.css" rel="stylesheet">
    <script src="/js/main.js"></script>

	<title>Index</title>
</head>
<body>
    <div class="panel">
        <nav class="navigation">
            <%@include file="boxes/nav.jsp" %>
        </nav>       
        <section class="page">
            <header class="header">
                <%@include file="boxes/header.jsp" %>
            </header>
            <section class="p-4 content">
                <header class="py-3">
                    <h1 class="ty-page-title">
                        <c:if test='${type == "personal"}'>Personal</c:if>
                        <c:if test='${type == "public"}'>Public</c:if>
                        <c:if test='${type == "search"}'>Search</c:if>
                    </h1>

                </header>
                <c:forEach items="${documents}" var="doc">
					<tr>
						<td>Document Name: ${doc.getName()}</td>
                        <td>Size: ${doc.getSize()}</td>
                        <td>Type: ${doc.getType()}</td>
                        <td>Owner: ${doc.getOwnerName()}</td>
                        <td><div align="center">
                            <button class="download" id="${doc.getId()}">Download</button>
                        </div></td>
					</tr>

				</c:forEach>
            </section>
            <footer>
                <%@include file="boxes/footer.jsp" %>
            </footer>
        </section>
    </div>
    <script type="module" src="/js/documents.js"></script>
</body>
</html>

