package com.company.awms.data.schedule;

import org.springframework.data.annotation.Id;

public class Task{
	
	@Id
	public String id;
	
	public String taskReceiverID;
	public String taskBody;
	public String taskTitle;
	
}