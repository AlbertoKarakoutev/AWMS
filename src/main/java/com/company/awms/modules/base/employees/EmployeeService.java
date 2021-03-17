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
import org.springframework.ui.Model;

import com.company.awms.modules.base.admin.data.Module;
import com.company.awms.modules.base.admin.data.ModuleRepo;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeDailyReference;
import com.company.awms.modules.base.employees.data.EmployeeRepo;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.schedule.data.Day;
import com.company.awms.modules.base.schedule.data.ScheduleRepo;
import com.company.awms.security.EmployeeDetails;

@Service
public class EmployeeService {
	private EmployeeRepo employeeRepo;
	private ModuleRepo moduleRepo;
	private ScheduleRepo scheduleRepo;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo, ModuleRepo moduleRepo, ScheduleRepo scheduleRepo, PasswordEncoder passwordEncoder) {
		this.employeeRepo = employeeRepo;
		this.moduleRepo = moduleRepo;
		this.scheduleRepo = scheduleRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public Employee getEmployee(String employeeID) throws IOException {
		Optional<Employee> employee = getRepo().findById(employeeID);

		if (employee.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employee.get();
	}

	public Employee getOwner() throws IOException {
		Optional<Employee> owner = this.getRepo().findByRole("OWNER");

		if (owner.isEmpty()) {
			return null;
		}
		return owner.get();
	}

	public Employee registerEmployee(String data) {
		Employee employee = new Employee();

		setEmployeeInfo(data, employee);

		String encodedPassword = this.passwordEncoder.encode(employee.getNationalID());
		employee.setPassword(encodedPassword);

		String message = "Your profile information has been updated by the Administrator.";
		new Notification(message).add("plain-notification").sendAndSave(employee, employeeRepo);

		getRepo().save(employee);
		return employee;
	}

	public Employee updatePassword(String newPassword, String employeeID) throws IOException {
		Employee employee = getEmployee(employeeID);

		String encodedPassword = this.passwordEncoder.encode(newPassword);
		employee.setPassword(encodedPassword);

		this.getRepo().save(employee);

		return employee;
	}

	public Employee updateEmployeeInfo(String employeeID, String data) throws IOException {
		Employee employee = getEmployee(employeeID);

		setEmployeeInfo(data, employee);

		String message = "Your profile information has been updated by the Administrator.";
		new Notification(message).add("plain-notification").sendAndSave(employee, employeeRepo);

		List<Day> schedule = scheduleRepo.findAll();
		dayLoop: for (Day day : schedule) {
			for (EmployeeDailyReference edr : day.getEmployees()) {
				if (edr.getIDRef().equals(employee.getID())) {
					if (!edr.getDepartment().equals(employee.getDepartment()) || edr.getLevel() != employee.getLevel()) {
						day.getEmployees().remove(edr);
						scheduleRepo.save(day);
						continue dayLoop;
					}
					edr.setFirstName(employee.getFirstName());
					edr.setLastName(employee.getLastName());
					edr.setNationalID(employee.getNationalID());
					scheduleRepo.save(day);
					break;
				}
			}
		}

		getRepo().save(employee);
		return employee;
	}

	public List<Employee> getManagers() {
		return this.getRepo().findAllByRole("MANAGER");
	}

	public List<Employee> getAllEmployeesDTOs() {
		List<Employee> employees = this.getRepo().findAll();
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
		List<Employee> employees = this.getRepo().findAll();

		if (employees.isEmpty()) {
			throw new IOException("Employee not found!");
		}

		return employees;
	}

	public List<Employee> getDepartmentEmployeesDTOs(String employeeID) throws IllegalAccessException {
		boolean notPermitted = true;
		Optional<Employee> downloader = getRepo().findById(employeeID);
		for (Employee manager : getRepo().findAllByRole("MANAGER")) {
			if (manager.getID().equals(employeeID)) {
				notPermitted = false;
			}
		}
		if (notPermitted)
			throw new IllegalAccessException();

		List<Employee> employees = getRepo().findByDepartment(downloader.get().getDepartment());
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

	public List<Module> getAllExtensionModules() {
		return moduleRepo.findByBase(false);
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
	
	public Boolean requestLeave(String employeeID, boolean paid, String startDate, String endDate) throws IOException {
		List<Employee> admins = getRepo().findAllByRole("ADMIN");
		if (admins.size() < 1) {
			throw new IOException();
		}
		Employee employee = getEmployee(employeeID);

		String paidStr = "paid";
		if (paid) {
			paidStr = "un" + paidStr;
		}
		String message = employee.getFirstName() + " " + employee.getLastName() + " has requested a " + paidStr + " leave in the period from " + startDate + " to " + endDate;

		for (Employee admin : admins) {
			new Notification(message).add("leave-request").add(employeeID).add(startDate).add(endDate).add(paid).sendAndSave(admin, employeeRepo);
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

		List<Employee> admins = getRepo().findAllByRole("ADMIN");
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
			getRepo().save(admin);
		}

		String message = "Your leave request for the period from " + startDateStr + " to " + endDateStr + " has been approved";
		new Notification(message).add("plain-notification").sendAndSave(employee, employeeRepo);

		getRepo().save(employee);

	}

	public void denyLeave(String employeeID, String startDateStr, String endDateStr) throws IOException {
		String message = "Your leave request for the period from " + startDateStr + " to " + endDateStr + " has been denied.";
		List<Employee> admins = getRepo().findAllByRole("ADMIN");
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
			getRepo().save(admin);
		}
		new Notification(message).add("plain-notification").sendAndSave(employeeID, getRepo());
	}

	public void deleteLeave(String employeeID, String leave) throws IOException {
		Employee employee = getEmployee(employeeID);

		LocalDate start = LocalDate.ofInstant(((Date) employee.getLeaves().get(Integer.parseInt(leave)).get("start")).toInstant(), ZoneId.systemDefault());
		LocalDate end = LocalDate.ofInstant(((Date) employee.getLeaves().get(Integer.parseInt(leave)).get("end")).toInstant(), ZoneId.systemDefault());

		String message = "Your leave for the period from " + start + " to " + end + " has been removed.";
		new Notification(message).add("plain-notification").sendAndSave(employee, employeeRepo);

		employee.getLeaves().remove(Integer.parseInt(leave));
		getRepo().save(employee);
	}

	public void deleteEmployee(String employeeID) {
		getRepo().deleteById(employeeID);

		List<Day> schedule = scheduleRepo.findAll();

		for (Day day : schedule) {
			for (EmployeeDailyReference edr : day.getEmployees()) {
				if (edr.getIDRef().equals(employeeID)) {
					day.getEmployees().remove(edr);
					scheduleRepo.save(day);
					break;
				}
			}
		}
	}

	public void updateSalary(double newSalary, Employee employee) {
		employee.setSalary(newSalary);
		this.getRepo().save(employee);
	}

	private void setEmployeeInfo(String data, Employee employee) {
		String[] dataValues = data.split("\\n");
		Map<String, String> newInfo = new HashMap<>();
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

	public void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails){
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		Employee user = null;
		try {
			user = getEmployee(employeeDetails.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int unread = 0;
		for (int i = 0; i < user.getNotifications().size(); i++) {
			if (!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}

		model.addAttribute("extModules", getExtensionModulesDTOs());
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

	public EmployeeRepo getRepo() {
		return employeeRepo;
	}
}
