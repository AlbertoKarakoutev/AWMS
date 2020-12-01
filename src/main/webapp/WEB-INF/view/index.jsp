<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta Content-Type="text/css" charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Insert title here</title>

    <link href="webjars/bootstrap/4.5.3/css/bootstrap.min.css" rel="stylesheet" />
    <link href="assets/css/main.css" rel="stylesheet" />
</head>
<body>
	
<h2 align="center"> Hello ${name}!</h2>
<h2>Current Time: ${time}</h2>
<div class="btn btn-primary"></div>

<!--Add main js file-->
<mvc:resources mapping="/webjars/**" location="classpath:/META-INF/resources/webjars/"/>
<script src="webjars/jquery/3.5.1/jquery.js"></script>
<script src="webjars/popper.js/1.16.0/umd/popper.js"></script>
<script src="webjars/bootstrap/4.5.3/js/bootstrap.js"></script>
<script src="assets/js/main.js"></script>
</body>
</html>
