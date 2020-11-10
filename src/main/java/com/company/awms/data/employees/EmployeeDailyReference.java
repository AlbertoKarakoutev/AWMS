package com.company.awms.data.employees;

import com.company.awms.data.schedule.Task;
import com.company.awms.services.EmployeeService;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
@Document
public class EmployeeDailyReference extends Employee {

	EmployeeRepo employeeRepo;
	
	// Work time should be in the {startHour, startMinutes, endHour, endMinutes} format
	public int[] workTime = new int[4];
	public String firstNameLocal;
	public String lastNameLocal;
	public ArrayList<Task> tasks = new ArrayList<Task>();
	public LocalDate date;

	public EmployeeDailyReference() {
	}

	public EmployeeDailyReference(String nationalID) {
		super(nationalID);
		try {
			this.firstNameLocal = employeeRepo.findByNationalID(nationalID).getFirstName();
			this.lastNameLocal = employeeRepo.findByNationalID(nationalID).getLastName();
		}catch(Exception e) {
			System.err.println("Error finding user!");
		}
	}
	
	public String getID() {
		return this.id;
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
