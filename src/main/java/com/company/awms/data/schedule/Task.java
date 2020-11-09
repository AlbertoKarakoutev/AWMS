package com.company.awms.data.schedule;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Task{
	
	@Id
	public String id;
	
	public String taskReceiverID;
	public String taskBody;
	public String taskTitle;

	public Task(String taskReceiverID, String taskBody, String taskTitle) {
		this.taskReceiverID = taskReceiverID;
		this.taskBody = taskBody;
		this.taskTitle = taskTitle;
	}

	public Task() {
	}

	public String getTaskReceiverID() {
		return taskReceiverID;
	}

	public String getTaskBody() {
		return taskBody;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskReceiverID(String taskReceiverID) {
		this.taskReceiverID = taskReceiverID;
	}

	public void setTaskBody(String taskBody) {
		this.taskBody = taskBody;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
}
