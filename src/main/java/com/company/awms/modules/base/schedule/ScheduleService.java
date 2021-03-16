package com.company.awms.modules.base.schedule;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.awms.modules.base.admin.data.Department;
import com.company.awms.modules.base.admin.data.DepartmentRepo;
import com.company.awms.modules.base.admin.data.ModuleRepo;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeDailyReference;
import com.company.awms.modules.base.employees.data.EmployeeRepo;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.schedule.data.Day;
import com.company.awms.modules.base.schedule.data.ScheduleRepo;
import com.company.awms.modules.base.schedule.data.Task;

@Service
public class ScheduleService {

	private ScheduleRepo scheduleRepo;
	private EmployeeRepo employeeRepo;
	private DepartmentRepo departmentRepo;

	private static List<Day> nextMonthDays;

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo, EmployeeRepo employeeRepo, DepartmentRepo departmentRepo, ModuleRepo moduleRepo) {

		this.scheduleRepo = scheduleRepo;
		this.employeeRepo = employeeRepo;
		this.departmentRepo = departmentRepo;

	}

	public Day getDay(LocalDate date) throws IOException {
		Optional<Day> dayOptional = scheduleRepo.findByDate(date);
		if (dayOptional.isEmpty()) {
			throw new IOException("Invalid date");
		}
		return dayOptional.get();
	}

	public void addWorkDays(String employeeNationalID, String dateStr, boolean onCall, String startShiftStr, String endShiftStr) throws Exception {

		LocalTime startShift = null;
		LocalTime endShift = null;

		try {
			startShift = LocalTime.parse(startShiftStr);
			endShift = LocalTime.parse(endShiftStr);
		} catch (Exception e) {
		}

		Day currentDay = null;

		LocalTime[] workTime = new LocalTime[2];

		int[] workTimeJSON;
		Department employeeLevel = null;

		EmployeeDailyReference employee = new EmployeeDailyReference(this.employeeRepo, employeeNationalID);

		Department thisDepartment = getDepartment(employee.getDepartment());
		int level = employee.getLevel();
		if (thisDepartment.isUniversalSchedule()) {
			workTimeJSON = thisDepartment.getDailyHours();
			employeeLevel = thisDepartment;
		} else {
			employeeLevel = thisDepartment.getLevel(level);
			workTimeJSON = employeeLevel.getDailyHours();
		}

		try {
			if (!onCall) {
				LocalTime start = LocalTime.of(workTimeJSON[0], workTimeJSON[1]);
				workTime[0] = start;
				LocalTime end = start.plus(employeeLevel.getShiftLength() + employeeLevel.getDailyBreakDurationTotal(), ChronoUnit.HOURS);
				workTime[1] = end;
			}
			employee.setWorkTime(workTime);
		} catch (Exception e) {
			System.out.println("DateTime error!");
			e.printStackTrace();
			return;
		}
		if (onCall) {
			workTime[0] = startShift;
			workTime[1] = endShift;
		}

		for (Day day : nextMonthDays) {
			if (day.getDate().toString().equals(dateStr)) {
				currentDay = day;
				break;
			}
		}
		if (currentDay.getDate().isBefore(LocalDate.now()))
			throw new Exception("This date has already passed!");

		if (currentDay.getEmployees() != null) {
			currentDay.addEmployee(employee);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(employee);
			currentDay.setEmployees(singleEmployee);
		}

		for (Day day : nextMonthDays) {
			if (day.getDate().toString().equals(dateStr))
				day = currentDay;
			break;
		}
	}

	public void addWorkDay(String employeeNationalID, String dateStr, boolean onCall, String startShiftStr, String endShiftStr) throws Exception {

		LocalTime startShift = null;
		LocalTime endShift = null;

		try {
			startShift = LocalTime.parse(startShiftStr);
			endShift = LocalTime.parse(endShiftStr);
		} catch (Exception e) {
		}

		Day currentDay = getDay(LocalDate.parse(dateStr));

		LocalTime[] workTime = new LocalTime[2];

		int[] workTimeJSON;
		Department employeeLevel = null;

		EmployeeDailyReference employee = new EmployeeDailyReference(this.employeeRepo, employeeNationalID);

		Department thisDepartment = getDepartment(employee.getDepartment());
		int level = employee.getLevel();
		if (thisDepartment.isUniversalSchedule()) {
			workTimeJSON = thisDepartment.getDailyHours();
			employeeLevel = thisDepartment;
		} else {
			employeeLevel = thisDepartment.getLevel(level);
			workTimeJSON = employeeLevel.getDailyHours();
		}

		try {
			if (!onCall) {
				LocalTime start = LocalTime.of(workTimeJSON[0], workTimeJSON[1]);
				workTime[0] = start;
				LocalTime end = start.plus(employeeLevel.getShiftLength() + employeeLevel.getDailyBreakDurationTotal(), ChronoUnit.HOURS);
				workTime[1] = end;
			}
			employee.setWorkTime(workTime);
		} catch (Exception e) {
			System.out.println("DateTime error!");
			e.printStackTrace();
			return;
		}
		if (onCall) {
			workTime[0] = startShift;
			workTime[1] = endShift;
		}

		if (currentDay.getDate().isBefore(LocalDate.now()))
			throw new Exception("This date has already passed!");

		if (currentDay.getEmployees() != null) {
			currentDay.addEmployee(employee);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(employee);
			currentDay.setEmployees(singleEmployee);
		}
		scheduleRepo.save(currentDay);
	}

	public void deleteWorkDay(String employeeNationalID, String date) throws Exception {
		Day selectedDay = getDay(LocalDate.parse(date));
		if (selectedDay.getDate().isBefore(LocalDate.now()))
			throw new Exception("This date had already passed!");
		for (EmployeeDailyReference edr : selectedDay.getEmployees()) {
			if (edr.getNationalID().equals(employeeNationalID)) {
				selectedDay.getEmployees().remove(edr);
				break;
			}
		}
		scheduleRepo.save(selectedDay);
	}

	public void declineSwap(String employeeID, LocalDate receiverDate) throws IOException {
		String message = "Your swap request for " + receiverDate + " has been declined.";
		new Notification(message).add("plain-notification").sendAndSave(employeeID, employeeRepo);
	}

	public void swapEmployees(String requesterNationalID, String receiverNationalID, String requesterDateParam, String receiverDateParam) throws IOException, NullPointerException {

		EmployeeDailyReference requester = null;
		EmployeeDailyReference receiver = null;

		Day requesterDay = getDay(LocalDate.parse(requesterDateParam).withDayOfMonth(LocalDate.parse(requesterDateParam).getDayOfMonth()));
		Day receiverDay = getDay(LocalDate.parse(receiverDateParam).withDayOfMonth(LocalDate.parse(receiverDateParam).getDayOfMonth()));

		for (EmployeeDailyReference edr : requesterDay.getEmployees()) {
			if (edr.getNationalID().equals(requesterNationalID)) {
				requester = edr;
			}
		}
		for (EmployeeDailyReference edr : receiverDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				receiver = edr;
			}
		}

		if (requester != null && receiver != null) {
			requesterDay.getEmployees().remove(requester);
			receiverDay.getEmployees().remove(receiver);

			LocalTime[] workTimeTemp = requester.getWorkTime();
			requester.setWorkTime(receiver.getWorkTime());
			receiver.setWorkTime(workTimeTemp);

			requesterDay.getEmployees().add(receiver);
			receiverDay.getEmployees().add(requester);

		} else {
			throw new NullPointerException("No such EDR in those days");
		}

		Employee requesterObj = employeeRepo.findByNationalID(requesterNationalID).get();
		String message = receiver.getFirstName() + " " + receiver.getLastName() + " has accepted your request to swap his/her " + LocalDate.parse(receiverDateParam) + " shift with your " + LocalDate.parse(requesterDateParam) + " shift.";
		new Notification(message).add("plain-notification").sendAndSave(requesterObj, employeeRepo);

		scheduleRepo.save(requesterDay);
		scheduleRepo.save(receiverDay);
	}

	public void swapRequest(String requesterID, String receiverNationalID, String requesterDateParam, String receiverDateParam) throws Exception {
	
		Optional<Employee> requesterOptional = employeeRepo.findById(requesterID);
		if (requesterOptional.isEmpty()) {
			throw new IOException("Requester not found!");
		}
		Employee requester = requesterOptional.get();

		Optional<Employee> receiverOptional = employeeRepo.findByNationalID(receiverNationalID);
		if (receiverOptional.isEmpty()) {
			throw new IOException("Receiver not found!");
		}
		Employee receiver = receiverOptional.get();

		if (LocalDate.parse(requesterDateParam).isBefore(LocalDate.now()) || LocalDate.parse(receiverDateParam).isBefore(LocalDate.now()))
			throw new Exception("This date had already passed!");
		Day receiverDay = getDay(LocalDate.parse(receiverDateParam));
		for (EmployeeDailyReference edr : receiverDay.getEmployees()) {
			if (requester.getNationalID().equals(edr.getNationalID())) {
				throw new Exception("The requester already has a shift in that day!");
			}
		}
		String message = "You have received a request from " + requester.getFirstName() + " " + requester.getLastName() + " to swap his/her " + LocalDate.parse(requesterDateParam) + " shift with your " + LocalDate.parse(receiverDateParam)
				+ " shift.";
		new Notification(message).add("swap-request").add(requester.getNationalID()).add(LocalDate.parse(requesterDateParam)).add(LocalDate.parse(receiverDateParam)).sendAndSave(receiver, employeeRepo);

	}

	public void addTask(String data) throws Exception {

		String[] dataValues = data.split("\\n");

		if (dataValues.length != 5) {
			throw new Exception("Invalid request");
		}
		Map<String, String> newInfo = new HashMap<>();
		for (String field : dataValues) {
			field = field.substring(0, field.length());
			newInfo.put(field.split("=")[0], field.split("=")[1]);
		}

		Day currentDay = getDay(LocalDate.parse(newInfo.get("date")));

		if (currentDay.getDate().isBefore(LocalDate.now()))
			throw new Exception("This date has already passed!");

		Task task;
		for (EmployeeDailyReference edr : currentDay.getEmployees()) {
			if (((String)newInfo.get("receiverNationalID")).equals(edr.getNationalID())) {
				task = new Task(currentDay, newInfo.get("title"), newInfo.get("body"));
				task.setTaskReward(Integer.parseInt(newInfo.get("reward")));
				if (edr.getTasks() != null) {
					edr.getTasks().add(task);
					this.scheduleRepo.save(currentDay);
				} else {
					ArrayList<Task> taskList = new ArrayList<>();
					taskList.add(task);
					edr.setTasks(taskList);
					this.scheduleRepo.save(currentDay);
				}
				Employee employee = employeeRepo.findByNationalID(newInfo.get("receiverNationalID")).get();
				
				String message = "You have a new task for " + newInfo.get("date");
				new Notification(message).add("plain-notification").sendAndSave(employee, employeeRepo);
				
				return;
			}
		}
		throw new Exception();
	}

	public void markTaskAsComplete(String employeeID, String taskNum, String dateStr) throws IOException {
		Day day = getDay(LocalDate.parse(dateStr));
		Optional<Employee> employeeOptional = employeeRepo.findById(employeeID);
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found");
		}
		String employeeNationalID = employeeOptional.get().getNationalID();

		for (EmployeeDailyReference edr : day.getEmployees()) {
			if (edr.getNationalID().equals(employeeNationalID)) {
				if (!StringUtils.isNumeric(taskNum)) {
					throw new NumberFormatException("Invalid task number");
				}
				if (edr.getTasks().size() <= Integer.parseInt(taskNum)) {
					throw new NullPointerException("Task doesn't exist");
				}
				Task task = edr.getTasks().get(Integer.parseInt(taskNum));
				if (!task.getCompleted())
					task.setCompleted(true);

				String message = edr.getFirstName() + " " + edr.getLastName() + " has marked assignment \"" + task.getTaskTitle() + "\" as completed.";

				List<Employee> managers = employeeRepo.findAllByRole("MANAGER");
				for (Employee manager : managers) {
					if (manager.getDepartment().equals(edr.getDepartment())) {
						new Notification(message).add("task-payment-request").add(employeeID).add(taskNum).add(dateStr).add(task).sendAndSave(manager, employeeRepo);
					}
				}
				scheduleRepo.save(day);
				break;
			}
		}

	}

	public void approveTask(String dateStr, String employeeID, String taskNum, String managerID) throws Exception {
		Day taskDay = getDay(LocalDate.parse(dateStr));

		Optional<Employee> employeeOptional = employeeRepo.findById(employeeID);
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found");
		}
		String employeeNationalID = employeeOptional.get().getNationalID();

		Optional<Employee> managerOptional = employeeRepo.findById(managerID);
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found");
		}
		if (!managerOptional.get().getRole().equals("MANAGER")) {
			throw new Exception("Employee is not a manager!");
		}

		for (EmployeeDailyReference edr : taskDay.getEmployees()) {
			if (edr.getNationalID().equals(employeeNationalID)) {
				if (!StringUtils.isNumeric(taskNum)) {
					throw new NumberFormatException("Invalid task number");
				}
				if (edr.getTasks().size() <= Integer.parseInt(taskNum)) {
					throw new NullPointerException("Task doesn't exist");
				}
				Task task = edr.getTasks().get(Integer.parseInt(taskNum));
				if (!task.getPaidFor())
					task.setPaidFor(true);

				List<Object> notificationData = new ArrayList<>();
				notificationData.add("plain-notification");
				String message = "Your have been rewarded for your task  \"" + task.getTaskTitle() + "\"";

				new Notification(message).add("plain-notification").sendAndSave(employeeOptional.get(), employeeRepo);

				List<Employee> managers = employeeRepo.findAllByRole("MANAGER");
				for (Employee manager : managers) {
					if (manager.getDepartment().equals(edr.getDepartment())) {
						notificationLoop: for (Notification notification : manager.getNotifications()) {
							List<Object> data = notification.getData();
							if (data.size() > 4) {
								boolean titles = ((Task) data.get(4)).getTaskTitle().equals(task.getTaskTitle());
								boolean bodies = ((Task) data.get(4)).getTaskBody().equals(task.getTaskBody());
								boolean rewards = ((Task) data.get(4)).getTaskReward() == task.getTaskReward();
								boolean equal = titles && bodies && rewards;
								if (equal) {
									manager.getNotifications().remove(notification);
									break notificationLoop;
								}
							}
						}
						employeeRepo.save(manager);
					}
				}
				scheduleRepo.save(taskDay);
				break;
			}
		}

	}

	public void resetTask(String dateStr, String employeeID, String taskNum, String managerID) throws Exception {
		Day taskDay = getDay(LocalDate.parse(dateStr));

		Optional<Employee> employeeOptional = employeeRepo.findById(employeeID);
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found");
		}
		String employeeNationalID = employeeOptional.get().getNationalID();

		Optional<Employee> managerOptional = employeeRepo.findById(managerID);
		if (employeeOptional.isEmpty()) {
			throw new IOException("Employee not found");
		}
		if (!managerOptional.get().getRole().equals("MANAGER")) {
			throw new Exception("Employee is not a manager!");
		}

		for (EmployeeDailyReference edr : taskDay.getEmployees()) {
			if (edr.getNationalID().equals(employeeNationalID)) {
				if (!StringUtils.isNumeric(taskNum)) {
					throw new NumberFormatException("Invalid task number");
				}
				if (edr.getTasks().size() <= Integer.parseInt(taskNum)) {
					throw new NullPointerException("Task doesn't exist");
				}
				Task task = edr.getTasks().get(Integer.parseInt(taskNum));
				if (!task.getPaidFor())
					task.setCompleted(false);

				String message = "Your work on task \"" + task.getTaskTitle() + "\" has not been approved";

				new Notification(message).add("plain-notification").sendAndSave(employeeOptional.get(), employeeRepo);

				List<Employee> managers = employeeRepo.findAllByRole("MANAGER");
				for (Employee manager : managers) {
					if (manager.getDepartment().equals(edr.getDepartment())) {
						notificationLoop: for (Notification notification : manager.getNotifications()) {
							List<Object> data = notification.getData();
							if (data.size() > 4) {
								boolean titles = ((Task) data.get(4)).getTaskTitle().equals(task.getTaskTitle());
								boolean bodies = ((Task) data.get(4)).getTaskBody().equals(task.getTaskBody());
								boolean rewards = ((Task) data.get(4)).getTaskReward() == task.getTaskReward();
								boolean equal = titles && bodies && rewards;
								if (equal) {
									manager.getNotifications().remove(notification);
									break notificationLoop;
								}
							}
						}
						employeeRepo.save(manager);
					}
				}
				scheduleRepo.save(taskDay);
				break;
			}
		}
	}

	public void addMonthlyDays() {
		LocalDate date = LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(1);
		List<Day> newDays = new ArrayList<Day>();
		for (int i = 1; i <= date.lengthOfMonth(); i++) {
			LocalDate correctDate = date.withDayOfMonth(i);
			try {
				getDay(correctDate);
			} catch (Exception e) {
				Day day = new Day(correctDate);
				newDays.add(day);
			}
		}
		scheduleRepo.saveAll(newDays);
	}

	public void clearMonthlyDays() {
		List<Day> toBeDeleted = new ArrayList<Day>();
		for (int date = 1; date <= LocalDate.now().plus(1, ChronoUnit.MONTHS).lengthOfMonth(); date++) {
			Optional<Day> day = scheduleRepo.findByDate(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(date));
			if (!day.isEmpty()) {
				toBeDeleted.add(day.get());
			}
		}
		scheduleRepo.deleteAll(toBeDeleted);
	}

	public void removeReadNotifications() {

		List<Employee> employees = employeeRepo.findAll();
		for (Employee employee : employees) {
			for (int i = 0; i < employee.getNotifications().size(); i++) {
				Notification notification = employee.getNotifications().get(i);
				if (notification.getRead()) {
					employee.getNotifications().remove(notification);
				}
			}
			employeeRepo.save(employee);
		}
	}

	@Scheduled(cron = "1 0 0 1 * *")
	public void applySchedule() throws Exception {

		removeReadNotifications();
		clearMonthlyDays();
		addMonthlyDays();

		nextMonthDays = new ArrayList<Day>();
		List<Day> allDays = scheduleRepo.findAll();
		for (Day day : allDays) {
			if (day.getDate().getMonthValue() == LocalDate.now().plusMonths(1).getMonthValue()) {
				nextMonthDays.add(day);
			}
		}

		// Add the employee shifts
		for (int i = 97; i < 123; i++) {
			String departmentCode = Character.toString((char) i);
			Department department = getDepartment(departmentCode);
			if (department == null) {
				continue;
			}
			if (department.isUniversalSchedule()) {
				switch (department.getScheduleType()) {
				case "Regular":
					applyRegularSchedule(departmentCode, 0);
					break;
				case "Irregular":
					applyIrregularSchedule(departmentCode, 0);
					break;
				case "OnCall":
					applyOnCallSchedule(departmentCode, 0);
					break;
				default:
					System.out.println("Type not found!");
					continue;
				}
			} else {
				for (int j = 0; j < department.getLevels().size(); j++) {
					Department departmentAtLevel = getDepartmentAtLevel(departmentCode, j);
					switch (departmentAtLevel.getScheduleType()) {
					case "Regular":
						applyRegularSchedule(departmentCode, j);
						break;
					case "Irregular":
						applyIrregularSchedule(departmentCode, j);
						break;
					case "OnCall":
						applyOnCallSchedule(departmentCode, j);
						break;
					default:
						System.out.println("Type not found!");
						continue;
					}
				}
			}
		}

		scheduleRepo.saveAll(nextMonthDays);

		List<Employee> allEmployees = employeeRepo.findAll();

		String message = "The schedule for " + YearMonth.from(LocalDate.now().plus(1, ChronoUnit.MONTHS)) + " has been updated.";
		for (Employee issuer : allEmployees) {
			new Notification(message).add("schedule-update").sendAndSave(issuer, employeeRepo);
		}
	}

	public boolean[] getSchedule(Employee viewer, YearMonth month) throws IOException {
		boolean[] employeeWorkDays = new boolean[32];
		for (int i = 1; i <= month.lengthOfMonth(); i++) {
			Day thisDay = getDay(month.atDay(i));
			if (thisDay.getEmployees().isEmpty()) {
				continue;
			}
			for (int j = 0; j < thisDay.getEmployees().size(); j++) {
				EmployeeDailyReference employee = thisDay.getEmployees().get(j);
				if (employee.getNationalID().equals(viewer.getNationalID())) {
					employeeWorkDays[i] = true;
				}
			}

		}

		return employeeWorkDays;
	}

	public List<String> getScheduleAfterDate(String employeeID, String dateStr, String receiverNationalID) throws IOException {

		LocalDate date = LocalDate.parse(dateStr);
		if (date.isBefore(LocalDate.now()))
			return null;

		Optional<Employee> viewerOptional = this.employeeRepo.findById(employeeID);
		if (viewerOptional.isEmpty()) {
			throw new IOException("Employee not found!");
		}
		Employee viewer = viewerOptional.get();

		List<String> employeeWorkDays = new ArrayList<String>();

		dayLoop: for (int i = 1; i <= date.lengthOfMonth(); i++) {
			if (i > date.getDayOfMonth()) {
				Day thisDay = getDay(date.withDayOfMonth(i));
				if (thisDay.getEmployees().isEmpty()) {
					continue;
				}
				List<String> dayNationalIDs = new ArrayList<String>();
				for (int j = 0; j < thisDay.getEmployees().size(); j++) {
					dayNationalIDs.add(thisDay.getEmployees().get(j).getNationalID());
				}
				for (int j = 0; j < dayNationalIDs.size(); j++) {
					if (dayNationalIDs.contains(viewer.getNationalID()) && !dayNationalIDs.contains(receiverNationalID)) {
						employeeWorkDays.add(date.withDayOfMonth(i).toString());
						continue dayLoop;
					}
				}
			}
		}

		if (date.getMonthValue() == LocalDate.now().getMonthValue()) {
			LocalDate nextMonth = date.plusMonths(1);
			for (int i = 1; i <= nextMonth.lengthOfMonth(); i++) {
				Day thisDay = getDay(nextMonth.withDayOfMonth(i));
				if (thisDay.getEmployees().isEmpty()) {
					continue;
				}
				List<String> dayNationalIDs = new ArrayList<String>();
				for (int j = 0; j < thisDay.getEmployees().size(); j++) {
					dayNationalIDs.add(thisDay.getEmployees().get(j).getNationalID());
				}
				for (int j = 0; j < dayNationalIDs.size(); j++) {
					if (dayNationalIDs.contains(viewer.getNationalID()) && !dayNationalIDs.contains(receiverNationalID)) {
						employeeWorkDays.add(nextMonth.withDayOfMonth(i).toString());
					}
				}
			}
		}

		return employeeWorkDays;
	}

	public List<EmployeeDailyReference> getDailySchedule(LocalDate date, String employeeID) throws IOException {
		List<EmployeeDailyReference> employees = new ArrayList<EmployeeDailyReference>();
		Optional<Employee> viewerOptional = employeeRepo.findById(employeeID);
		Employee viewer = null;
		if (viewerOptional.isEmpty()) {
			throw new IOException();
		} else {
			viewer = viewerOptional.get();
		}

		Day day = getDay(date);

		if (viewer.getRole().equals("ADMIN")) {
			for (EmployeeDailyReference edr : day.getEmployees()) {
				employees.add(edr);
			}
		} else if (viewer.getRole().equals("EMPLOYEE")) {
			for (EmployeeDailyReference edr : day.getEmployees()) {
				if (edr.getDepartment().equals(viewer.getDepartment()) && edr.getLevel() <= viewer.getLevel()) {
					employees.add(edr);
				}
			}
		} else if (viewer.getRole().equals("MANAGER")) {
			for (EmployeeDailyReference edr : day.getEmployees()) {
				if (edr.getDepartment().equals(viewer.getDepartment())) {
					employees.add(edr);
				}
			}
		}
		return employees;
	}

	public List<Task> getDailyTasks(LocalDate date, String employeeID) throws IOException {
		List<Task> tasks = new ArrayList<Task>();
		
		Day taskDay = getDay(date);
		
		EmployeeDailyReference thisEDR = null;

		edrLoop: 
		for (EmployeeDailyReference edr : taskDay.getEmployees()) {
			if (edr.getIDRef().equals(employeeID)) {
				thisEDR = edr;
				break edrLoop;
			}
		}
		if (thisEDR != null) {
			if (thisEDR.getTasks() != null) {
				tasks = thisEDR.getTasks();
			}
		}

		return tasks;
	}

	public boolean applyOnCallSchedule(String department, int level) throws Exception {
		Department thisDepartment = getDepartment(department);
		if (!thisDepartment.isUniversalSchedule()) {
			thisDepartment = getDepartmentAtLevel(department, level);
		}
		if (thisDepartment == null)
			return false;

		List<Employee> employees = employeeRepo.findByAccessLevel(department + Integer.toString(level));
		if (employees.isEmpty()) {
			System.out.println("No employees in this department!");
			return false;
		}

		LocalDate month = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		int lengthOfMonth = YearMonth.of(month.getYear(), month.getMonth()).lengthOfMonth();

		Integer shiftLength;
		Integer breakBetweenShiftsMin;
		Integer employeesPerShift;
		Integer monthlyWorkDays;
		double lengthOfWorkDay;
		int[] dailyHours;
		try {
			dailyHours = thisDepartment.getDailyHours();
			shiftLength = thisDepartment.getShiftLength();
			breakBetweenShiftsMin = thisDepartment.getBreakBetweenShifts();
			lengthOfWorkDay = ((double) dailyHours[2] + ((double) dailyHours[3] / 60)) - ((double) dailyHours[0] + ((double) dailyHours[1] / 60));
			if (lengthOfWorkDay > 23.9) {
				lengthOfWorkDay = 24;
			}
			employeesPerShift = thisDepartment.getEmployeesPerShift();

			monthlyWorkDays = thisDepartment.getMonthlyWorkDays();

		} catch (Exception e) {
			System.out.println("JSON Error");
			e.printStackTrace();
			return false;
		}

		class Shift {
			LocalDateTime beginning;
			LocalDateTime end;

			public Shift(LocalDateTime beginning, LocalDateTime end) {
				this.beginning = beginning;
				this.end = end;
			}

			public LocalDate getDate() {
				return this.beginning.toLocalDate();
			}
		}
		if (lengthOfMonth / monthlyWorkDays < 2)
			System.out.println("Shifts required are over 15, adding shifts each day");
		ArrayList<Day> workDays = new ArrayList<>();
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == month.getMonthValue()) {
				if (day.getDate().getDayOfMonth() % (lengthOfMonth / monthlyWorkDays) == 0) {
					workDays.add(day);
				}
			}
		}
		ArrayList<Shift> shifts = new ArrayList<>();
		for (Day day : workDays) {
			LocalDateTime shiftStart = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours[0], dailyHours[1], 0, 0));
			LocalDateTime shiftEnd = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours[2], dailyHours[3], 1, 0));
			do {
				shifts.add(new Shift(LocalDateTime.of(day.getDate(), shiftStart.toLocalTime()), LocalDateTime.of(day.getDate(), shiftStart.plusHours(shiftLength).toLocalTime())));

				shiftStart = shiftStart.plusHours(shiftLength);
			} while (shiftStart.isBefore(shiftEnd.plusMinutes(2)) && LocalDate.from(shiftStart) == day.getDate());
		}

		long shiftsPerDay = (long) (lengthOfWorkDay / shiftLength);
		long employeesPerDay = employeesPerShift * shiftsPerDay;
		long breakBetweenShifts = (long) (((employees.size() - 1) * shiftLength) / employeesPerShift + ((employees.size() / employeesPerShift) / shiftsPerDay) * (24 - lengthOfWorkDay));

		if (monthlyWorkDays > lengthOfMonth) {
			System.out.println("Too many work days per month!");
			return false;
		}
		if (lengthOfWorkDay % shiftLength != 0) {
			System.out.println("Impossible to distribute shifts correctly");
			return false;
		}
		if (shifts.size() / (double) employees.size() < 2.0) {
			System.out.println("Unnccessary amount of employees in this department");
			return false;
		}
		if (employeesPerDay > employees.size()) {
			System.out.println("Employees per day required are " + employeesPerDay + ", but only " + employees.size() + " can be distributed");
			return false;
		} else {
			if (breakBetweenShifts < breakBetweenShiftsMin) {
				System.out.println("Break between shifts should be " + breakBetweenShiftsMin + ", but is " + breakBetweenShifts);
				return false;
			}
		}

		for (Employee employee : employees) {
			int initialOffset = employees.indexOf(employee) % employees.size();
			shiftLoop: for (int i = initialOffset; i < shifts.size(); i += employees.size() / employeesPerShift) {
				Day day = getDay(shifts.get(i).getDate());
				if (isLeaveDay(employee, day)) {
					continue shiftLoop;
				} else {
					addWorkDays(employee.getNationalID(), day.getDate().toString(), true, shifts.get(i).beginning.toLocalTime().toString(), shifts.get(i).end.toLocalTime().toString());
				}
			}
		}
		return true;
	}

	public boolean applyIrregularSchedule(String department, int level) throws Exception {
		ArrayList<Day> month = new ArrayList<>();
		LocalDate now = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == now.getMonthValue()) {
				month.add(day);
			}
		}

		LocalDate lastMonth = now.minus(1, ChronoUnit.MONTHS);
		int lengthOfPreviousMonth = YearMonth.of(now.getYear(), lastMonth.getMonth()).lengthOfMonth();
		for (Employee employee : this.employeeRepo.findByAccessLevel(department + Integer.toString(level))) {

			// Getting the last 7 days of the previous month into boolean array,
			// representing
			// did-work and did-not-work values to properly continue the work schedule
			boolean[] lastSevenDays = new boolean[7];
			workWeekLoop: for (LocalDate date = lastMonth.withDayOfMonth(lengthOfPreviousMonth); date.isAfter(lastMonth.withDayOfMonth(lengthOfPreviousMonth - 7)); date = date.plus(-1, ChronoUnit.DAYS)) {
				Day thisDay = getDay(date);
				for (EmployeeDailyReference edrl : thisDay.getEmployees()) {
					if (edrl.getNationalID().equals(employee.getNationalID())) {
						lastSevenDays[(date.getDayOfMonth() - (lengthOfPreviousMonth - 7)) - 1] = true;
						continue workWeekLoop;
					}
				}
			}

			// Determining if the last days were work days. If so, determining how many they
			// were
			int consecutiveBreakDays = 0;
			int consecutiveWorkDays = 0;
			for (int i = lastSevenDays.length - 1; i >= 0; i--) {
				if (lastSevenDays[i]) {
					consecutiveWorkDays++;
				} else {
					break;
				}
			}

			// If the last days were break days, determining how much they were
			if (consecutiveWorkDays == 0) {
				for (int i = lastSevenDays.length - 1; i >= 0; i--) {
					if (!lastSevenDays[i]) {
						consecutiveBreakDays++;
					} else {
						break;
					}
				}
			}

			int workWeekCounter = 0;
			boolean[] employeeWorkWeek = new boolean[employee.getWorkWeek()[0] + employee.getWorkWeek()[1]];
			for (int i = 0; i < employee.getWorkWeek()[0]; i++) {
				employeeWorkWeek[i] = true;
			}
			dayLoop: for (Day day : month) {
				if (consecutiveWorkDays == 0) {
					if (consecutiveBreakDays < employee.getWorkWeek()[1]) {
						consecutiveBreakDays++;
						continue dayLoop;
					}
				} else {
					if (consecutiveWorkDays < employee.getWorkWeek()[0]) {
						if (!isLeaveDay(employee, day)) {
							addWorkDays(employee.getID(), day.getDate().toString(), false, null, null);
						}
					}
				}

				if (!isLeaveDay(employee, day)) {
					if (employeeWorkWeek[workWeekCounter]) {
						addWorkDays(employee.getID(), day.getDate().toString(), false, null, null);
					}
					if (workWeekCounter + 1 >= employeeWorkWeek.length) {
						workWeekCounter = 0;
					} else {
						workWeekCounter++;
					}

				}
			}
		}
		return true;
	}

	public boolean applyRegularSchedule(String department, int level) throws Exception {

		ArrayList<Day> month = new ArrayList<>();
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == LocalDate.now().plus(1, ChronoUnit.MONTHS).getMonthValue()) {
				month.add(day);
			}
		}

		for (Employee employee : this.employeeRepo.findByAccessLevel(department + Integer.toString(level))) {
			for (Day day : month) {
				if (day.getDate().getDayOfWeek() != DayOfWeek.SUNDAY && day.getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
					if (!isLeaveDay(employee, day)) {
						addWorkDays(employee.getNationalID(), day.getDate().toString(), false, null, null);
					}
				}
			}
		}
		return true;
	}

	public boolean isLeaveDay(Employee employee, Day day) {

		if (!employee.getLeaves().isEmpty()) {
			for (Map<String, Object> leave : employee.getLeaves()) {
				try {
					if (day.getDate().isAfter((LocalDate) leave.get("start")) && day.getDate().isBefore((LocalDate) leave.get("end"))) {
						return true;
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		return false;
	}

	public Department getDepartmentAtLevel(String department, int level) throws Exception {

		Department thisDepartment = getDepartment(department);
		return thisDepartment.getLevel(level);
	}

	public Department getDepartment(String departmentCode) throws Exception {

		Optional<Department> department = departmentRepo.findByDepartmentCode(departmentCode);
		if (department.isEmpty()) {
			return null;
		}
		return department.get();
	}

	public List<Department> getAllDepartments() {
		return this.departmentRepo.findAll();
	}

	@SuppressWarnings("unchecked")
	public void setDepartment(Object departmentObj) throws IOException, ParseException {
		JSONObject departmentJSON = new JSONObject((Map<Object, String>) departmentObj);
		List<Department> departments = departmentRepo.findAll();
		List<String> dptCodeSet = new ArrayList<String>();
		for (Department dpt : departments) {
			dptCodeSet.add(Character.toString(dpt.getDepartmentCode()));
		}
		String key = (String) departmentJSON.get("departmentCode");
		// Check if it is a new department
		if (key == null || key.equals("")) {
			// Check if it is the first department
			if (dptCodeSet.size() == 0) {
				key = "a";
			} else {
				// Check if the admin has specified a dpt. code
				int firstCode = 97;
				while (dptCodeSet.contains(String.valueOf(Character.toChars(firstCode)))) {
					firstCode++;
				}
				if (firstCode < 123) {
					key = String.valueOf(Character.toChars(firstCode)).replace("\"", "");
				} else {
					return;
				}

			}
		}
		Department department;
		try {
			Optional<Department> departmentOptional = departmentRepo.findById((String) departmentJSON.get("id"));
			if (!departmentOptional.isEmpty()) {
				department = departmentOptional.get();

				department.setName((String) departmentJSON.get("name"));
				department.setDepartmentCode(key.charAt(0));
			} else {
				department = new Department(key.charAt(0), (String) departmentJSON.get("name"), Boolean.parseBoolean((String) departmentJSON.get("universalSchedule")));
			}
		} catch (Exception e) {
			department = new Department(key.charAt(0), (String) departmentJSON.get("name"), Boolean.parseBoolean((String) departmentJSON.get("universalSchedule")));
		}

		if (department.isUniversalSchedule()) {
			try {
				department.update(departmentJSON);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// for(Object levelObj : (ArrayList)departmentJSON.get("levels")) {
			for (int i = 0; i < ((ArrayList) departmentJSON.get("levels")).size(); i++) {
				Object levelObj = ((ArrayList) departmentJSON.get("levels")).get(i);
				JSONObject levelJSON = new JSONObject((Map<Object, String>) levelObj);
				Department level = new Department();
				level.update(levelJSON);
				try {
					department.setLevel(i, level);
				} catch (Exception e) {
					department.addLevel(level);
				}
			}
		}
		departmentRepo.save(department);
	}

	public void deleteDepartment(String departmentCode) throws Exception {
		Department toBeDeleted = getDepartment(departmentCode);
		departmentRepo.deleteById(toBeDeleted.getID());
	}

}
