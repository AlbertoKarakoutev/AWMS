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
    <%@include file="boxes/nav.jsp" %>
    <section class="page">
      <header class="header">
        <%@include file="boxes/header.jsp" %>
      </header>

      <section class="content">
        <div class="container text-center">
          <h2 class="document mb-5 ">Documents</h2>
          <div class="doc-container row row-cols-auto py-2">
            
            <c:forEach items="${documents}" var="document">
              <div class="col">
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" value="" id="defaultCheck1">
                  <label class="form-check-label" for="defaultCheck1">
                    ${document.getName()}
                  </label>
                </div>
              </div>
              <div class="col-3">
                ${document.getData()}
              </div>
              <div class="col-2">
                <button class="btn btn-dark download py-1">Download</button>
              </div>
            </c:forEach>
          </div>
        </div>
      </section>

      <footer>
        <%@include file="boxes/footer.jsp" %>
      </footer>
    </section>
    </section>
  </div>
  <script type="module" src="/js/main.js"></script>
</body>

</html>