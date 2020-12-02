<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta Content-Type="text/css" charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="webjars/bootstrap/4.5.3/css/bootstrap.min.css" rel="stylesheet" />
    <link href="css/main.css" rel="stylesheet">

	<title>Insert title here</title>
</head>
<body>
    <div class="panel">
        <div class="navigation">
            <%@include file="boxes/nav.jsp" %>
        </div>
        <div class="page">
            <h2 align="center"> Hello ${name}!</h2>
            <h2>Current Time: ${time}</h2>
            <div class="btn btn-primary">Button</div>
        </div>
    </div>

<!--Add main js file-->
<script src="webjars/jquery/3.5.1/jquery.js"></script>
<script src="webjars/popper.js/1.16.0/umd/popper.js"></script>
<script src="webjars/bootstrap/4.5.3/js/bootstrap.js"></script>
<script src="assets/js/main.js"></script>
</body>
</html>
