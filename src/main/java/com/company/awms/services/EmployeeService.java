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

	public List<Employee> getManagers() {
		return this.employeeRepo.findAllByRole("MANAGER");
	}

	public void setNotificationRead(String employeeID, int notificationNumber) throws IOException {
		Optional<Employee> employee = employeeRepo.findById(employeeID);
		if(employee.isEmpty()) {
			throw new IOException("Employee not found!");
		}else {
			employee.get().getNotifications().get(notificationNumber).setRead(true);
			employeeRepo.save(employee.get());
		}
		
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

	public Employee updatePassword(String newPassword, String employeeID) throws IOException {
		Employee employee = getEmployee(employeeID);

		String encodedPassword = this.passwordEncoder.encode(newPassword);
		employee.setPassword(encodedPassword);

		this.employeeRepo.save(employee);

		return employee;
	}

	public void updateEmployeeInfo(String employeeID, String data) throws IOException {
		Employee employee = getEmployee(employeeID);
		// TODO:
		// Validation? from Validator Class
		String[] dataValues = data.split("\\n");
		Map<String, String> newInfo = new HashMap<String, String>();
		for(String field : dataValues) {
			newInfo.put(field.split("=")[0], field.split("=")[1]);
		}
		employee.setFirstName(newInfo.get("firstName"));
		employee.setLastName(newInfo.get("familyName"));
		employee.setNationalID(newInfo.get("nationalID"));
		employee.setEmail(newInfo.get("email"));
		employee.setIBAN(newInfo.get("iban"));
		employee.setPhoneNumber(newInfo.get("phoneNumber"));
		employee.setDepartment(newInfo.get("department").split(":")[0]);
		try {
			employee.setLevel(Integer.parseInt(newInfo.get("level")));
		}catch(Exception e) {
			System.out.println("Level is 0");
			employee.setLevel(0);
		}
		
        List<Object> notificationData = new ArrayList<Object>();
        notificationData.add("info-updated");
		String message = "Your profile information has been updated by the Administrator.";
		employee.getNotifications().add(new Notification(message, notificationData));
        
		this.employeeRepo.save(employee);
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
