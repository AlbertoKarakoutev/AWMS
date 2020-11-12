package com.company.awms.data.schedule;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Task {

	@Id
	private String id;

	private String receiverNationalID;
	private String taskBody;
	private String taskTitle;
	private boolean completed;
	private boolean paidFor;
	private double taskReward = 0;

	public Task(String receiverNationalID, Day date, String taskTitle, String taskBody) {
		this.receiverNationalID = receiverNationalID;
		this.taskBody = taskBody;
		this.taskTitle = taskTitle;
	}

	public Task() {
	}

	public String getID() {
		return this.id;
	}
	
	public String getTaskReceiverNationalID() {
		return receiverNationalID;
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
	
	public double getTaskReward() {
		return taskReward;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setPaidFor(boolean paidFor) {
		this.paidFor = paidFor;
	}
	
	public void setTaskReward(double taskReward) {
		this.taskReward = taskReward;
	}

	public void setTaskReceiverNationalID(String receiverNationalID) {
		this.receiverNationalID = receiverNationalID;
	}

	public void setTaskBody(String taskBody) {
		this.taskBody = taskBody;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
}
