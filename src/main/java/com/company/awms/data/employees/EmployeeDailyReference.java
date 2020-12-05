package com.company.awms.data.employees;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.awms.data.schedule.Task;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
public class EmployeeDailyReference extends Employee {

	private EmployeeRepo employeeRepo;
	
	// Work time should be in the {startHour, startMinutes, endHour, endMinutes} format
	private LocalTime[] workTime;
	private String refFirstName;
	private String refLastName;
	private String refNationalID;
	private List<Task> tasks = new ArrayList<>();
	private LocalDate date;

	/*public EmployeeDailyReference() {}

	@Autowired
	public EmployeeDailyReference(EmployeeRepo employeeRepo) {
		this.employeeRepo = employeeRepo;
	}*/

	@Autowired
	public EmployeeDailyReference(EmployeeRepo employeeRepo, String nationalID) throws IOException {
		this.employeeRepo = employeeRepo;
		this.refNationalID = nationalID;

		Optional<Employee> employee = this.employeeRepo.findByNationalID(nationalID);

		if(employee.isEmpty()) {
			throw new IOException("Employee not found!");
		} else {
			this.refFirstName = employee.get().getFirstName();
			this.refLastName = employee.get().getLastName();
			this.workTime = new LocalTime[2];
		}
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public String getRefNationalID() {
		return this.refNationalID;
	}
	
	public String getWorkTimeInfo() {
		return String.format("%s:%s - %s:%s", workTime[0], workTime[1], workTime[2], workTime[3]);
	}

	public LocalTime[] getWorkTime() {
		return this.workTime;
	}

	public String getRefFirstName() {
		return refFirstName;
	}

	public String getRefLastName() {
		return refLastName;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setRefFirstName(String refFirstName) {
		this.refFirstName = refFirstName;
	}
	
	public void setRefLastName(String refLastName) {
		this.refLastName = refLastName;
	}
	
	public void setWorkTime(LocalTime[] workTime) {
		this.workTime = workTime;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
