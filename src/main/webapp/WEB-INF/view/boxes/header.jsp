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
		<div class="ap-badge-position">
			<span class="badge badge-danger">3</span>
		</div>
	</button>
	<div class="dropdown-menu" aria-labelledby="dropdownNotification">
		<%
		List<Notification> notifications = (List<Notification>)request.getAttribute("notifications");
		boolean allRead = true;
		if(notifications.size()>0){
			for(int i = 0; i < notifications.size(); i++){ 
				Notification notification = notifications.get(i);
				if(notification.getRead()==false){
					allRead = false;
					if(!notification.getRead()){
						System.out.println(notification.getMessage());
						switch (notification.getData().get(0).toString()) {
						case "swap-request":
							LocalDate requesterDate = LocalDate.ofInstant(((Date)notification.getData().get(2)).toInstant(), ZoneId.systemDefault());
							LocalDate receiverDate = LocalDate.ofInstant(((Date)notification.getData().get(3)).toInstant(), ZoneId.systemDefault());%>
						    <div class="dropdown-item border-bottom">
							<a href="/schedule/?month=<%=YearMonth.now()%>" class="text-decoration-none text-justify"> 
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</a>
							<div class="d-flex">
							<form action="/schedule/swap/?noteNum=<%=Integer.toString(i)%>&requesterNationalID=<%=notification.getData().get(1)%>&requesterDate=<%=requesterDate.toString()%>&receiverDate=<%=receiverDate.toString()%>" method="POST">
								<input type="submit" class="btn btn-success btn-sm" id="accept" value="Accept"/>
							</form>
							<form action="/schedule/decline" class="ml-auto">
								<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
								<input type="submit" class="btn-danger btn btn-sm" id="decline" value="Decline"/>
							</form>
							</div>
							</div>
							<%break;
						case "new-reply":
							ForumReply newReply = (ForumReply)notification.getData().get(2);%>
							<div class="dropdown-item border-bottom">
							<form action="/forum/dismiss">
								<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
								<%-- <input type="submit" class="btn-dark btn btn-sm" id="dismiss" value="Dismiss"/> --%>
								<button class="btn text-left p-0">
								    <span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								    <br>
								    <%=notification.getMessage()%>
								</button>
							</form>
							</div>
							<%break; 
						case "new-thread":
							ForumThread newThread = (ForumThread)notification.getData().get(2);%>
							<a  class="dropdown-item" href="/forum/thread/<%=newThread.getID()%>"> 
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</a>
							<form style="float:right;" action="/forum/dismiss">
								<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
								<input style="display:inline-block" type="submit" class="btn-dark btn btn-sm" id="dismiss" value="Dismiss"/>
							</form>
							<%break; 
						case "info-updated":%>
							<a class="dropdown-item" href="/"> 
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								<br>
								<%=notification.getMessage()%>
							</a>
							<form style="float:right;" action="/dismiss">
								<input type="hidden" name="noteNum" value="<%=Integer.toString(i)%>">
								<input style="display:inline-block" type="submit" class="btn-dark btn btn-sm" id="dismiss" value="Dismiss"/>
							</form>
							<%break;
						}
					}
            	}
			}
		}
		if(notifications.size()<=0 || allRead == true){%>
			<a class="dropdown-item" href="#"> No new notifications! </a>
		<%}%>
	</div>
</div>

<script src="/js/schedule.js"></script>