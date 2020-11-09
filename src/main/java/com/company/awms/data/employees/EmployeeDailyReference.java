package com.company.awms.data.employees;

import com.company.awms.data.schedule.Task;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
@Document
public class EmployeeDailyReference extends Employee {

	// Work time should be in the {startHour, startMinutes, endHour, endMinutes} format
	public int[] workTime = new int[4];
	public ArrayList<Task> tasks = new ArrayList<Task>();
	public String date;

	public EmployeeDailyReference() {
	}

	public EmployeeDailyReference(String nationalID) {
		super(nationalID);
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	public String getWorkTimeInfo() {
		return String.format("%d:%d - %d:%d", workTime[0], workTime[1], workTime[2], workTime[3]);
	}

	public int[] getWorkTime() {
		return this.workTime;
	}

	public String getDate() {
		return date;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setWorkTime(int[] workTime) {
		this.workTime = workTime;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
}
