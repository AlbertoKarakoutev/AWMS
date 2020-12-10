package com.company.awms.data.employees;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.company.awms.data.schedule.Task;

//A reference to an existing employee, containing his/her work hours for a specific day and tasks, that he has to perform
public class EmployeeDailyReference {

	private LocalTime[] workTime;
	private String firstName;
	private String lastName;
	private String nationalID;
	private List<Task> tasks = new ArrayList<>();
	private LocalDate date;

	public EmployeeDailyReference() {}

	/*@Autowired
	public EmployeeDailyReference(EmployeeRepo employeeRepo) {
		this.employeeRepo = employeeRepo;
	}*/

	public EmployeeDailyReference(EmployeeRepo employeeRepo, String nationalID) throws IOException {
		this.nationalID = nationalID;

		Optional<Employee> employee = employeeRepo.findByNationalID(nationalID);

		if(employee.isEmpty()) {
			throw new IOException("Employee not found!");
		} else {
			this.firstName = employee.get().getFirstName();
			this.lastName = employee.get().getLastName();
			this.workTime = new LocalTime[2];
		}
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public String getNationalID() {
		return this.nationalID;
	}
	
	public String getWorkTimeInfo() {
		return String.format("%s - %s", workTime[0], workTime[1]);
	}

	public LocalTime[] getWorkTime() {
		return this.workTime;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
