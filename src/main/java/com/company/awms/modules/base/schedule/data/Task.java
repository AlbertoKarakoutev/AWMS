package com.company.awms.modules.base.schedule.data;

import org.springframework.data.annotation.Id;

public class Task {

	@Id
	private String id;

	private String taskBody;
	private String taskTitle;
	private boolean completed;
	private boolean paidFor;
	private double taskReward = 0;

	public Task(Day date, String taskTitle, String taskBody) {
		this.taskBody = taskBody;
		this.taskTitle = taskTitle;
	}

	public Task() {
	}

	public String getID() {
		return this.id;
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

	public void setTaskBody(String taskBody) {
		this.taskBody = taskBody;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
}
