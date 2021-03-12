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
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    <script type="module" src="/js/main.js"></script>
	<title>Login</title>
</head>
<body>
<div class="bg-light h-100">
  <div class="container-sm shadow-sm bg-white py-4">
  <h1 class="text-center">Login</h1>
  <form method="POST" action="/login" id="login">
  <div class="form-group">
    <label for="username">Email</label>
    <input type="email" class="form-control" id="username" name="username" aria-describedby="username" placeholder="Email...">
  </div>
  <div class="form-group">
    <label for="passowrd">Password</label>
    <input type="password" class="form-control" id="passowrd" name="password" placeholder="Password">
  </div>
  <button type="submit" class="btn btn-primary">Login</button>
</form>
  </div>
  </div>
</body>