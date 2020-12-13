package com.company.awms.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.employees.Notification;

@Service
public class EmployeeService {
	private EmployeeRepo employeeRepo;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo, PasswordEncoder passwordEncoder) {
		this.employeeRepo = employeeRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public Employee getEmployee(String employeeID) throws IOException {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if (employee.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employee.get();
	}

	public List<Employee> getAllEmployees() throws IOException {
		List<Employee> employees = this.employeeRepo.findAll();

		if (employees.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employees;
	}

	public List<Employee> searchEmployees(List<Employee> employees, String searchTerm, String type) {
		List<Employee> foundEmployees = new ArrayList<>();

		Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);

		employeeLoop: for (Employee employee : employees) {
			Matcher matcher;
			switch (type) {
			case "ID":
				matcher = pattern.matcher(employee.getID());
				break;
			case "nationalID":
				matcher = pattern.matcher(employee.getNationalID());
				break;
			case "firstName":
				matcher = pattern.matcher(employee.getFirstName());
				break;
			case "lastName":
				matcher = pattern.matcher(employee.getLastName());
				break;
			case "role":
				matcher = pattern.matcher(employee.getRole());
				break;
			case "email":
				matcher = pattern.matcher(employee.getEmail());
				break;
			case "iban":
				matcher = pattern.matcher(employee.getIBAN());
				break;
			case "level":
				matcher = pattern.matcher(Integer.toString(employee.getLevel()));
				break;
			case "department":
				matcher = pattern.matcher(employee.getDepartment());
				break;
			case "accessLevel":
				matcher = pattern.matcher(employee.getAccessLevel());
				break;
			default:
				continue employeeLoop;
			}
			boolean matchFound = matcher.find();
			if(matchFound) {
				foundEmployees.add(employee);
			}
		}

		return foundEmployees;
	}

	public Employee getOwner() throws IOException {
		Optional<Employee> owner = this.employeeRepo.findByRole("OWNER");

		if (owner.isEmpty()) {
			throw new IOException("Owner does not exist in database!");
		}

		return owner.get();
	}

	public List<Employee> getManagers() throws IOException {
		return this.employeeRepo.findAllByRole("MANAGER");
	}

	public void registerEmployee(Employee newEmployee) {
		// TODO:
		// Validation? from Validator Class
		// Add salt to password
		String encodedPassword = this.passwordEncoder.encode(newEmployee.getPassword());
		newEmployee.setPassword(encodedPassword);
		newEmployee.setRole("EMPLOYEE");

		this.employeeRepo.save(newEmployee);
	}

	public void updateEmployeeInfo(Employee newEmployee, String oldEmployeeID) throws IOException {
		Employee oldEmployee = getEmployee(oldEmployeeID);
		// TODO:
		// Validation? from Validator Class
		oldEmployee.setEmail(newEmployee.getEmail());
		oldEmployee.setFirstName(newEmployee.getFirstName());
		oldEmployee.setIBAN(newEmployee.getIBAN());
		oldEmployee.setLastName(newEmployee.getLastName());
		oldEmployee.setNationalID(newEmployee.getNationalID());
		oldEmployee.setPhoneNumber(newEmployee.getPhoneNumber());
		
        List<Object> notificationData = new ArrayList<Object>();
        notificationData.add("info-updated");
		String message = "Your profile information has been updated by the Administrator.";
		oldEmployee.getNotifications().add(new Notification(message, notificationData));
        
		this.employeeRepo.save(oldEmployee);
	}

	// Create a reference for this employee with information about his work hours
	// and date
	public EmployeeDailyReference createEmployeeDailyReference(Employee employee, LocalDate date, LocalTime[] workTime) throws IOException {
		EmployeeDailyReference empDayRef = new EmployeeDailyReference(employeeRepo, employee.getNationalID());
		empDayRef.setDate(date);
		empDayRef.setWorkTime(workTime);
		return empDayRef;
	}

	public void addLeave(String employeeID, LocalDate start, LocalDate end, boolean paid) throws IOException {
		Map<String, Object> leave = new HashMap<>();
		leave.put("Start", start);
		leave.put("End", end);
		leave.put("Paid", paid);
		Optional<Employee> employee = employeeRepo.findById(employeeID);

		if (employee.isEmpty()) {
			throw new IOException("Invalid employeeID!");
		}
		employee.get().getLeaves().add(leave);

		employeeRepo.save(employee.get());
	}

}
