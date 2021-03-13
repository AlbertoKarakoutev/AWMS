package com.company.awms.modules.base.employees;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.awms.modules.base.admin.data.Module;
import com.company.awms.modules.base.admin.data.ModuleRepo;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeRepo;
import com.company.awms.modules.base.employees.data.Notification;

@Service
public class EmployeeService {
	private EmployeeRepo employeeRepo;
	private ModuleRepo moduleRepo;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo, ModuleRepo moduleRepo, PasswordEncoder passwordEncoder) {
		this.employeeRepo = employeeRepo;
		this.moduleRepo = moduleRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public Employee getEmployee(String employeeID) throws IOException {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if (employee.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employee.get();
	}

	public List<Employee> getAllEmployeesDTOs() {
		List<Employee> employees = this.employeeRepo.findAll();
		List<Employee> employeeDTOs = new ArrayList<Employee>();
		for (Employee employee : employees) {
			Employee employeeDTO = new Employee();
			employeeDTO.setFirstName(employee.getFirstName());
			employeeDTO.setLastName(employee.getLastName());
			employeeDTO.setDepartment(employee.getDepartment());
			employeeDTO.setId(employee.getID());
			employeeDTOs.add(employeeDTO);
		}
		return employeeDTOs;
	}

	public List<Employee> getAllEmployees() throws IOException {
		List<Employee> employees = this.employeeRepo.findAll();

		if (employees.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employees;
	}

	public List<Employee> getDepartmentEmployeesDTOs(String employeeID) throws IllegalAccessException {
		boolean notPermitted = true;
		Optional<Employee> downloader = employeeRepo.findById(employeeID);
		for (Employee manager : employeeRepo.findAllByRole("MANAGER")) {
			if (manager.getID().equals(employeeID)) {
				notPermitted = false;
			}
		}
		if (notPermitted)
			throw new IllegalAccessException();

		List<Employee> employees = employeeRepo.findByDepartment(downloader.get().getDepartment());
		List<Employee> employeeDTOs = new ArrayList<Employee>();
		for (Employee employee : employees) {
			Employee employeeDTO = new Employee();
			employeeDTO.setFirstName(employee.getFirstName());
			employeeDTO.setLastName(employee.getLastName());
			employeeDTO.setDepartment(employee.getDepartment());
			employeeDTO.setId(employee.getID());
			employeeDTOs.add(employeeDTO);
		}
		return employeeDTOs;
	}

	public List<Employee> searchEmployees(List<Employee> employees, String searchTerm, String type) {
		List<Employee> foundEmployees = new ArrayList<>();
		Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		for (Employee employee : employees) {
			switch (type) {
			case "ID":
				matcher = pattern.matcher(employee.getID());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "NATIONAL ID":
				matcher = pattern.matcher(employee.getNationalID());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "FIRST NAME":
				matcher = pattern.matcher(employee.getFirstName());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "LAST NAME":
				matcher = pattern.matcher(employee.getLastName());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "ROLE":
				matcher = pattern.matcher(employee.getRole());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "E-MAIL":
				matcher = pattern.matcher(employee.getEmail());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "IBAN":
				matcher = pattern.matcher(employee.getIBAN());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "LEVEL":
				matcher = pattern.matcher(Integer.toString(employee.getLevel()));
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "DEPARTMENT CODE":
				matcher = pattern.matcher(employee.getDepartment());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			case "ACCESS LEVEL":
				matcher = pattern.matcher(employee.getAccessLevel());
				if (matcher.find()) {
					foundEmployees.add(employee);
				}
				break;
			default:
				continue;
			}
		}
		return foundEmployees;
	}

	public Employee getOwner() throws IOException {
		Optional<Employee> owner = this.employeeRepo.findByRole("OWNER");

		if (owner.isEmpty()) {
			return null;
		}
		return owner.get();
	}

	public List<Employee> getManagers() {
		return this.employeeRepo.findAllByRole("MANAGER");
	}

	public Boolean requestLeave(String employeeID, boolean paid, String startDate, String endDate) throws IOException {
		List<Employee> admins = employeeRepo.findAllByRole("ADMIN");
		if (admins.size() < 1) {
			throw new IOException();
		}
		Employee employee = getEmployee(employeeID);

		List<Object> notificationData = new ArrayList<>();
		notificationData.add("leave-request");
		notificationData.add(employeeID);
		notificationData.add(startDate);
		notificationData.add(endDate);
		notificationData.add(paid);
		String paidStr = "paid";
		if (paid) {
			paidStr = "un" + paidStr;
		}
		String message = employee.getFirstName() + " " + employee.getLastName() + " has requested a " + paidStr + " leave in the period from " + startDate + " to " + endDate;

		for (Employee admin : admins) {
			admin.getNotifications().add(new Notification(message, notificationData));
			employeeRepo.save(admin);
		}

		return paid;
	}

	public void approveLeave(String employeeID, boolean paid, String startDateStr, String endDateStr) throws IOException {
		Employee employee = getEmployee(employeeID);

		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalDate endDate = LocalDate.parse(endDateStr);
		Map<String, Object> leave = new HashMap<>();
		leave.put("start", startDate);
		leave.put("end", endDate);
		leave.put("paid", paid);
		employee.getLeaves().add(leave);

		List<Employee> admins = employeeRepo.findAllByRole("ADMIN");
		if (admins.size() < 1) {
			throw new IOException();
		}

		for (Employee admin : admins) {
			notificationLoop: for (Notification notification : admin.getNotifications()) {
				if (notification.getData().size() < 5)
					continue;
				if (notification.getData().get(1).equals(employeeID) && notification.getData().get(2).toString().equals(startDateStr) && notification.getData().get(3).toString().equals(endDateStr)) {
					admin.getNotifications().remove(notification);
					break notificationLoop;
				}
			}
			employeeRepo.save(admin);
		}

		employeeRepo.save(employee);

		String message = "Your leave request for the period from " + startDateStr + " to " + endDateStr + " has been approved";
		notify(employeeID, message, false);
	}

	public void denyLeave(String employeeID, String startDateStr, String endDateStr) throws IOException {
		String message = "Your leave request for the period from " + startDateStr + " to " + endDateStr + " has been denied.";
		List<Employee> admins = employeeRepo.findAllByRole("ADMIN");
		if (admins.size() < 1) {
			throw new IOException();
		}
		for (Employee admin : admins) {
			notificationLoop: for (Notification notification : admin.getNotifications()) {
				if (notification.getData().size() < 5)
					continue;
				if (notification.getData().get(1).equals(employeeID) && notification.getData().get(2).toString().equals(startDateStr) && notification.getData().get(3).toString().equals(endDateStr)) {
					admin.getNotifications().remove(notification);
					break notificationLoop;
				}
			}
			employeeRepo.save(admin);
		}
		notify(employeeID, message, false);
	}

	public void deleteLeave(String employeeID, String leave) throws IOException {
		Employee employee = getEmployee(employeeID);

		LocalDate start = LocalDate.ofInstant(((Date) employee.getLeaves().get(Integer.parseInt(leave)).get("start")).toInstant(), ZoneId.systemDefault());
		LocalDate end = LocalDate.ofInstant(((Date) employee.getLeaves().get(Integer.parseInt(leave)).get("end")).toInstant(), ZoneId.systemDefault());

		List<Object> notificationData = new ArrayList<>();
		notificationData.add("plain-notification");
		String message = "Your leave for the period from " + start + " to " + end + " has been removed.";
		employee.getNotifications().add(new Notification(message, notificationData));

		employee.getLeaves().remove(Integer.parseInt(leave));
		employeeRepo.save(employee);
	}

	public void setNotificationRead(String employeeID, int notificationNumber) throws IOException {
		Optional<Employee> employee = employeeRepo.findById(employeeID);
		if (employee.isEmpty()) {
			throw new IOException("Employee not found!");
		} else {
			employee.get().getNotifications().get(notificationNumber).setRead(true);
			employeeRepo.save(employee.get());
		}

	}

	public Employee registerEmployee(String data) {
		Employee employee = new Employee();

		setEmployeeInfo(data, employee);

		String encodedPassword = this.passwordEncoder.encode(employee.getNationalID());
		employee.setPassword(encodedPassword);

		List<Object> notificationData = new ArrayList<>();
		notificationData.add("plain-notification");
		String message = "Your profile information has been updated by the Administrator.";
		employee.getNotifications().add(new Notification(message, notificationData));

		this.employeeRepo.save(employee);
		return employee;
	}

	public Employee updatePassword(String newPassword, String employeeID) throws IOException {
		Employee employee = getEmployee(employeeID);

		String encodedPassword = this.passwordEncoder.encode(newPassword);
		employee.setPassword(encodedPassword);

		this.employeeRepo.save(employee);

		return employee;
	}

	public void updateSalary(double newSalary, Employee employee) {
		employee.setSalary(newSalary);
		this.employeeRepo.save(employee);
	}

	public Employee updateEmployeeInfo(String employeeID, String data) throws IOException {
		Employee employee = getEmployee(employeeID);

		setEmployeeInfo(data, employee);

		String message = "Your profile information has been updated by the Administrator.";
		notify(employeeID, message, false);

		this.employeeRepo.save(employee);
		return employee;
	}

	private void setEmployeeInfo(String data, Employee employee) {
		String[] dataValues = data.split("\\n");
		Map<String, String> newInfo = new HashMap<>();
		System.out.println(data);
		for (String field : dataValues) {
			field = field.substring(0, field.length() - 1);
			newInfo.put(field.split("=")[0], field.split("=")[1]);
		}
		employee.setFirstName(newInfo.get("firstName"));
		employee.setLastName(newInfo.get("familyName"));
		employee.setNationalID(newInfo.get("nationalID"));
		employee.setEmail(newInfo.get("email"));
		employee.setIBAN(newInfo.get("iban"));
		employee.setPhoneNumber(newInfo.get("phoneNumber"));
		employee.setDepartment(newInfo.get("department").split(":")[0]);
		employee.setRole(newInfo.get("role"));
		employee.setPayPerHour(Double.parseDouble(newInfo.get("payPerHour")));
		try {
			employee.setLevel(Integer.parseInt(newInfo.get("level")));
		} catch (Exception e) {
			System.out.println("Level is 0");
			employee.setLevel(0);
		}
	}

	private void notify(String employeeID, String message, boolean searchByNationalID) throws IOException {
		Optional<Employee> employeeOptional;
		if (searchByNationalID) {
			employeeOptional = employeeRepo.findByNationalID(employeeID);
		} else {
			employeeOptional = employeeRepo.findById(employeeID);
		}
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found!");
		}
		Employee employee = employeeOptional.get();
		List<Object> notificationData = new ArrayList<>();
		notificationData.add("plain-notification");
		employee.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(employee);
	}

	public Module getModule(String name) throws IOException {
		Optional<Module> moduleOptional = moduleRepo.findByName(name);
		if (moduleOptional.isEmpty()) {
			throw new IOException();
		}
		return moduleOptional.get();
	}

	public List<Module> getAllModules() {
		return moduleRepo.findAll();
	}

	public Map<String, Boolean> getExtensionModulesDTOs() {
		Map<String, Boolean> conditions = new HashMap<String, Boolean>();
		List<Module> modules = getAllExtensionModules();
		for (Module module : modules) {
			StringBuilder moduleName = new StringBuilder(module.getName());
			moduleName.setCharAt(0, Character.toUpperCase(moduleName.charAt(0)));
			String name = moduleName.toString();
			conditions.put(name, module.isActive());
		}
		return conditions;
	}

	public List<Module> getAllExtensionModules() {
		return moduleRepo.findByBase(false);
	}

	public void updateModule(Module module) {
		moduleRepo.save(module);
	}
}
