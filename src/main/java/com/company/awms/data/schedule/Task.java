package com.company.awms.data.schedule;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Task {

	@Id
	public String id;

	public String taskReceiverID;
	public String taskBody;
	public String taskTitle;
	public boolean completed;
	public boolean paidFor;

	public Task(String taskReceiverID, String taskBody, String taskTitle) {
		this.taskReceiverID = taskReceiverID;
		this.taskBody = taskBody;
		this.taskTitle = taskTitle;
	}

	public Task() {
	}

	public String getID() {
		return this.id;
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

	public boolean getCompleted() {
		return this.completed;
	}

	public boolean getPaidFor() {
		return this.paidFor;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setPaidFor(boolean paidFor) {
		this.paidFor = paidFor;
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
