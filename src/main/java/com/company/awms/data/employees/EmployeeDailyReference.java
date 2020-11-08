package com.company.awms.data.employees;

import com.company.awms.data.schedule.Task;

import java.util.ArrayList;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
public class EmployeeDailyReference extends Employee{
	
	//Work time should be in the {startHour, startMinutes, endHour, endMinutes} format
	public int[] workTime = new int[4];
	public ArrayList<Task> tasks = new ArrayList<Task>();
	public String date;
	public int nationalID;
	
	public EmployeeDailyReference(int nationalID) {
		super(nationalID);
		this.nationalID = nationalID;
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public String getWorkTime() {
		return String.format("%d:%d - %d:%d", workTime[0], workTime[1], workTime[2], workTime[3]);
	}	
	
	public void setDate(String date) {
		this.date = date;
	}
	public void setWorkTime(int[] workTime) {
		this.workTime = workTime;
	}
}
