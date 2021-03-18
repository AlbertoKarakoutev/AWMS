<%@	page import="java.time.YearMonth"%>
<%@	page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@	taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<nav class="navbar navbar-default navbar-expand-lg">
	<button class="navbar-toggler" type="button" data-toggle="collapse"
		data-target="#navigation" aria-controls="navigation"
		aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"> <i class="fas fa-bars"></i>
		</span>
	</button>
	<div class="collapsed navbar-collapse" id="navigation">
		<header class="user-container p-2">
			<div class="avatar-container">
				<div class="small-avatar"></div>
			</div>
			<div class="user-info-container">
				<div class="user-name">${employeeName}</div>
				<div class="user-email">${employeeEmail}</div>
			</div>
		</header>
		<ul class="vertical-menu">
			<li class="vertical-item"><a href="/" title="Dashboard">Dashboard</a>
			</li>
			<li class="vertical-item"><a href="/contacts" title="Contacts">Contacts</a>
			</li>
			<li class="vertical-item schedule"><a
				href="/schedule/?month=<%=YearMonth.now().toString()%>"
				title="Working Shedule">Schedule</a></li>
			<li class="vertical-item"><a href="/employee/leaves" title="Leaves">Leaves</a>
			</li>
			<li class="vertical-item">
				<div class="dropdown">
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"
						role="button" aria-haspopup="true" aria-expanded="false"
						title="Forum">Forum</a>
					<div class="dropdown-menu">
						<a class="dropdown-item pl-4" title="Module" href="/forum">All
							topics</a> <a class="dropdown-item pl-4" title="Module"
							href="/forum/employee/threads/${employeeID}">My topics</a> <a
							class="dropdown-item pl-4" title="Module" href="/forum/answered">Answered</a>
						<a class="dropdown-item pl-4" title="Module"
							href="/forum/unanswered">Unanswered</a>

					</div>
				</div>
			</li>
			<li class="vertical-item">
				<div class="dropdown">
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"
						role="button" aria-haspopup="true" aria-expanded="false"
						title="Documents">Documents</a>
					<div class="dropdown-menu">
						<a class="dropdown-item pl-4" title="Module"
							href="/document/public">Public</a> <a
							class="dropdown-item pl-4" title="Module"
							href="/document/personal">Personal</a>
					</div>
				</div>
			</li>
			
			<%
			Map<String, Boolean> extensionModules = (Map<String, Boolean>)request.getAttribute("extModules");
			 
			if(extensionModules.size()>0){
				Iterator<String> iterator = extensionModules.keySet().iterator();
				while(iterator.hasNext()){
					String key = iterator.next();
					if(extensionModules.get(key)){%>
						<li class="vertical-item <%=key%>" style="display:inline"><a href="/<%=key.toLowerCase()%>" title="<%=key%>"><%=key%></a></li>
					<%}else{%>
						<li class="vertical-item <%=key%>" style="display:none"><a href="/<%=key.toLowerCase()%>" title="<%=key%>"><%=key%></a></li>
			<%}}}%>
			
			<sec:authentication property="principal.authorities" var="role"/>
			<c:if test="${role == '[ADMIN]'}">
				<li class="vertical-item"><a href="/admin/employee/all"
					title="Employees">Employees</a></li>
				<li class="vertical-item"><a href="/admin/modules"
					title="Modules">Modules</a></li>
				<li class="vertical-item"><a href="/admin/departments"
					title="Departments">Departments</a></li>
			</c:if>
			<sec:authentication property="principal.authorities" var="role"/>
			<c:if test="${role == '[MANAGER]'}">
				<li class="vertical-item"><a href="/employee/manager/department/?managerID=${employeeID}"
					title="Employees">Employees</a></li>
			</c:if>



		</ul>
		<footer class="button">
			<a href="/logout" type="button" class="btn sign-out-btn"
				title="Sing out">SING OUT</a>
		</footer>
	</div>
</nav>
