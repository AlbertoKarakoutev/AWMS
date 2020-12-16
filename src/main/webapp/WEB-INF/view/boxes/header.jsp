<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.company.awms.data.employees.Notification"%>
<%@page import="com.company.awms.data.forum.ForumThread"%>
<%@page import="com.company.awms.data.forum.ForumReply"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.time.*"%>
<div class="logo p-2 ">
	<h1 class="ty-logo">AWMS</h1>
	<button class="btn btn-outline-light ap-base-button"
		id="dropdownNotification" data-toggle="dropdown" aria-haspopup="true"
		aria-expanded="false">
		<i class="fas fa-bell"></i>
		<c:if test="${unread>0}">
			<div class="ap-badge-position">
				<span class="badge badge-danger">${unread}</span>
			</div>
		</c:if>
	</button>
	<div class="dropdown-menu" aria-labelledby="dropdownNotification">
		<%
		List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
		boolean allRead = true;
		if (notifications.size() > 0) {
			for (int i = 0; i < notifications.size(); i++) {
				Notification notification = notifications.get(i);
				if (notification.getRead() == false) {
			allRead = false;
			if (!notification.getRead()) {
				switch (notification.getData().get(0).toString()) {
				case "swap-request":
					LocalDate requesterDate = LocalDate.ofInstant(((Date) notification.getData().get(2)).toInstant(), ZoneId.systemDefault());
					LocalDate receiverDate = LocalDate.ofInstant(((Date) notification.getData().get(3)).toInstant(), ZoneId.systemDefault());%>
					<div class="dropdown-item border-bottom">
						<a href="/schedule/?month=<%=YearMonth.now()%>"
							class="text-decoration-none text-justify"> <span
							class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
							<br> <%=notification.getMessage()%>
						</a>
						<div class="d-flex">
							<form action="/schedule/swap/?noteNum=<%=Integer.toString(i)%>&requesterNationalID=<%=notification.getData().get(1)%>&requesterDate=<%=requesterDate.toString()%>&receiverDate=<%=receiverDate.toString()%>"
								method="POST">
								<input type="submit" class="btn btn-success btn-sm" id="accept"	value="Accept" />
							</form>
							<form action="/schedule/decline" class="ml-auto">
								<input type="hidden" name="noteNum"	value="<%=Integer.toString(i)%>"> 
								<input type="hidden" name="date" value="<%=requesterDate%>">
								<input type="hidden" name="receiverNationalID" value="<%=notification.getData().get(1)%>">
								<input type="submit" class="btn-danger btn btn-sm" id="decline" value="Decline" />
							</form>
						</div>
					</div>
				<%break;
				case "leave-request":
					String startDate = (String)notification.getData().get(2);
					String endDate = (String)notification.getData().get(3);%>
					<div class="dropdown-item border-bottom">
						<a href="/schedule/?month=<%=YearMonth.now()%>"
							class="text-decoration-none text-justify"> <span
							class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
							<br> <%=notification.getMessage()%>
						</a>
						<div class="d-flex">
							<form action="/admin/employee/approveLeave?noteNum=<%=Integer.toString(i)%>&employeeID=<%=notification.getData().get(1)%>&paid=<%=notification.getData().get(4)%>&startDate=<%=startDate%>&endDate=<%=endDate%>"
								method="POST">
								<input type="submit" class="btn btn-success btn-sm" id="approve" value="Approve"/>
							</form>
							<form action="/admin/employee/denyLeave?noteNum=<%=Integer.toString(i)%>&employeeID=<%=notification.getData().get(1)%>&paid=<%=notification.getData().get(4)%>&startDate=<%=startDate%>&endDate=<%=endDate%>"
								method="POST">
								<input type="submit" class="btn btn-success btn-sm" id="deny" value="Deny"/>
							</form>
						</div>
					</div>
				<%break;
				case "new-reply":
					ForumReply newReply = (ForumReply) notification.getData().get(2);%>
					<div class="dropdown-item border-bottom">
						<form action="/forum/dismiss/<%=newReply.getThreadID()%>">
							<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
							<button class="btn text-left p-0">
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</button>
						</form>
					</div>
				<%break;
				case "new-thread":
					ForumThread newThread = (ForumThread) notification.getData().get(2);%>
					<div class="dropdown-item border-bottom">
						<form action="/forum/dismiss/<%=newThread.getID()%>">
							<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
							<button class="btn text-left p-0">
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</button>
						</form>
					</div>
				<%break;
				case "plain-notification":%>
					<div class="dropdown-item border-bottom">
						<form action="/employee/dismiss">
							<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
							<button style="white-space:normal;" class="btn text-left p-0">
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</button>
						</form>
					</div>
				<%break;
		}
		}
		}
		}
		}
		if (notifications.size() <= 0 || allRead == true) {
		%>
		<a class="dropdown-item" href="#"> No new notifications! </a>
		<%
			}
		%>
	</div>
</div>

<script src="/js/schedule.js"></script>