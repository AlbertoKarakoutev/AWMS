package com.company.awms.data.employees;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import com.company.awms.data.schedule.Task;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
@Document
public class EmployeeDailyReference extends Employee {

	@Autowired
	EmployeeRepo employeeRepo;
	
	// Work time should be in the {startHour, startMinutes, endHour, endMinutes} format
	public int[] workTime = new int[4];
	public String refFirstName;
	public String refLastName;
	public String refNationalID;
	public ArrayList<Task> tasks = new ArrayList<Task>();
	public LocalDate date;

	public EmployeeDailyReference() {}
	
	public EmployeeDailyReference(EmployeeRepo employeeRepo) {
		this.employeeRepo = employeeRepo;
	}

	public EmployeeDailyReference(EmployeeRepo employeeRepo, String nationalID) {
		this.employeeRepo = employeeRepo;
		this.refNationalID = nationalID;
		try {
			this.refFirstName = employeeRepo.findByNationalID(nationalID).getFirstName();
			this.refLastName = employeeRepo.findByNationalID(nationalID).getLastName();
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Error finding edr!");
		}
	}
	
	public String getNationalID() {
		return this.refNationalID;
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

	public LocalDate getDate() {
		return date;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setWorkTime(int[] workTime) {
		this.workTime = workTime;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
}
