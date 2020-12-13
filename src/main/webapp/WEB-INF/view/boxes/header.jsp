<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.company.awms.data.employees.Notification"%>
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
		<c:if test="${notifications.size()>0}">
			<%for(int i = 0; i < ((List<Notification>)request.getAttribute("notifications")).size(); i++){ 
				Notification notification = ((List<Notification>)request.getAttribute("notifications")).get(i);
				if(notification.getRead()==false){
					if(!notification.getRead()){
						System.out.println(notification.getMessage());
						switch (notification.getData().get(0).toString()) {
						case "swap-request":
							LocalDate requesterDate = LocalDate.ofInstant(((Date)notification.getData().get(2)).toInstant(), ZoneId.systemDefault());
							LocalDate receiverDate = LocalDate.ofInstant(((Date)notification.getData().get(3)).toInstant(), ZoneId.systemDefault());%>
						
							<a class="dropdown-item" href="/schedule/?month=<%=YearMonth.now()%>"> 
								<span class="badge badge-secondary"><%=notification.getDateTime().toString().replace("T", " ").substring(0, 16)%></span>
								</br>
								<%=notification.getMessage()%>
							</a>
							<form action="/schedule/swap/?noteNum=<%=Integer.toString(i)%>&requesterNationalID=<%=notification.getData().get(1)%>&requesterDate=<%=requesterDate.toString()%>&receiverDate=<%=receiverDate.toString()%>" method="POST">
								<input type="submit" class="btn" id="accept" value="Accept"/>
							</form>
							<form action="/schedule/decline/?noteNum=<%=Integer.toString(i)%>" method="GET">
								<input type="submit" class="btn" id="decline" value="Decline"/>
							</form>
						<%}
					}
            	}
			}%>
		</c:if>
		<c:if test="${notifications.size()<=0}">
			<a class="dropdown-item" href="#"> No new notifications! </a>
            	update info
            	enw schedule
            	forum
		</c:if>
	</div>
</div>

<script src="/js/schedule.js"></script>