package com.company.awms.services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.employees.Notification;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;
import com.company.awms.data.schedule.Task;

@Service
public class ScheduleService {

	private ScheduleRepo scheduleRepo;
	private EmployeeRepo employeeRepo;

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo, EmployeeRepo employeeRepo) {

		this.scheduleRepo = scheduleRepo;
		this.employeeRepo = employeeRepo;
	}

	public Day getDay(LocalDate date) throws IOException {
		Optional<Day> dayOptional = scheduleRepo.findByDate(date);
		if (dayOptional.isEmpty()) {
			throw new IOException("Invalid date");
		}
		return dayOptional.get();
	}

	public void addWorkDay(String employeeNationalID, String dateStr, boolean onCall, String startShiftStr, String endShiftStr) throws IOException {

		LocalTime startShift = null;
		LocalTime endShift = null;
		
		try {
			startShift = LocalTime.parse(startShiftStr);
			endShift = LocalTime.parse(endShiftStr);
		}catch(Exception e) {}
		
		Day currentDay;
		LocalTime[] workTime = new LocalTime[2];

		int[] workTimeJSON;
		JSONObject employeeLevel = null;

		EmployeeDailyReference employee = new EmployeeDailyReference(this.employeeRepo, employeeNationalID);
		
		JSONObject thisDepartment = getDepartment(employee.getDepartment());
		int level = employee.getLevel();
		if (Boolean.parseBoolean((String) thisDepartment.get("Universal schedule"))) {
			workTimeJSON = Stream.of(((String) thisDepartment.get("Daily hours")).split(",")).mapToInt(Integer::parseInt).toArray();
			employeeLevel = thisDepartment;
		} else {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject arrayElement = (JSONObject) levels.get(level);
			employeeLevel = (JSONObject) arrayElement.get(Integer.toString(level));
			workTimeJSON = Stream.of(((String) employeeLevel.get("Daily hours")).split(",")).mapToInt(Integer::parseInt).toArray();
		}

		try {
			if (!onCall) {
				LocalTime start = LocalTime.of(workTimeJSON[0], workTimeJSON[1]);
				workTime[0] = start;
				LocalTime end = start.plus(Integer.parseInt(employeeLevel.get("Shift length").toString()) + Integer.parseInt(employeeLevel.get("Daily break duration total").toString()), ChronoUnit.HOURS);
				workTime[1] = end;
			} 
			employee.setWorkTime(workTime);
		} catch (Exception e) {
			System.out.println("DateTime error!");
			e.printStackTrace();
			return;
		}
		if(onCall) {
			workTime[0] = startShift;
			workTime[1] = endShift;
		}
		
		currentDay = getDay(LocalDate.parse(dateStr));

		if (currentDay.getEmployees() != null) {
			currentDay.addEmployee(employee);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(employee);
			currentDay.setEmployees(singleEmployee);
		}

		scheduleRepo.save(currentDay);
	}

	public void deleteWorkDay(String employeeNationalID, String date) throws IOException {
		Day selectedDay = getDay(LocalDate.parse(date));
		for (EmployeeDailyReference edr : selectedDay.getEmployees()) {
			if (edr.getNationalID().equals(employeeNationalID)) {
				selectedDay.getEmployees().remove(edr);
				break;
			}
		}
		scheduleRepo.save(selectedDay);
	}

	public void declineSwap(String employeeID, LocalDate receiverDate) throws IOException {
		Optional<Employee> receiverOptional = employeeRepo.findByNationalID(employeeID);
		if (receiverOptional.isEmpty()) {
			throw new IOException("No such employee!");
		}
		Employee receiver = receiverOptional.get();
		List<Object> notificationData = new ArrayList<Object>();
		notificationData.add("plain-notification");
		String message = "Your swap request for " + receiverDate + "has been declined.";
		receiver.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(receiver);
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

		List<Object> notificationData = new ArrayList<>();
		Employee requesterObj = employeeRepo.findByNationalID(requesterNationalID).get();
		notificationData.add("plain-notification");
		String message = receiver.getFirstName() + " " + receiver.getLastName() + " has accepted your request to swap his/her " + LocalDate.parse(receiverDateParam) + " shift with your " + LocalDate.parse(requesterDateParam) + " shift.";
		requesterObj.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(requesterObj);

		scheduleRepo.save(requesterDay);
		scheduleRepo.save(receiverDay);
	}

	public void swapRequest(String requesterID, String receiverNationalID, String requesterDateParam, String receiverDateParam) throws IOException {
		List<Object> notificationData = new ArrayList<>();

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

		notificationData.add("swap-request");
		notificationData.add(requester.getNationalID());
		notificationData.add(LocalDate.parse(requesterDateParam));
		notificationData.add(LocalDate.parse(receiverDateParam));
		String message = "You have received a request from " + requester.getFirstName() + " " + requester.getLastName() + " to swap his/her " + LocalDate.parse(requesterDateParam) + " shift with your " + LocalDate.parse(receiverDateParam) + " shift.";
		receiver.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(receiver);

	}

	public void addTask(String data) throws Exception {

		String[] dataValues = data.split("\\n");

		if(dataValues.length != 5) {
			throw new Exception("Invalid request");
		}
		Map<String, String> newInfo = new HashMap<>();
		for (String field : dataValues) {
			field = field.substring(0, field.length() - 1);
			newInfo.put(field.split("=")[0], field.split("=")[1]);
		}
		
		Day currentDay = getDay(LocalDate.parse(newInfo.get("date")));
		
		Task task;
		for (EmployeeDailyReference edr : currentDay.getEmployees()) {
			if (edr.getNationalID().equals(newInfo.get("receiverNationalID"))) {
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
				List<Object> notificationData = new ArrayList<>();
				notificationData.add("plain-notification");
				String message = "You have a new task for " + newInfo.get("date");
				employee.getNotifications().add(new Notification(message, notificationData));
				employeeRepo.save(employee);
			}
		}
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
				if(!StringUtils.isNumeric(taskNum)) {
					throw new NumberFormatException("Invalid task number");
				}
				Task task = edr.getTasks().get(Integer.parseInt(taskNum));
				if (!task.getCompleted())
					task.setCompleted(true);
				List<Employee> managers = employeeRepo.findAllByRole("MANAGER");
				for (Employee manager : managers) {
					if (manager.getDepartment().equals(edr.getDepartment())) {
						List<Object> notificationData = new ArrayList<>();
						notificationData.add("plain-notification");
						String message = edr.getFirstName() + " " + edr.getLastName() + "has marked assignment \"" + task.getTaskTitle() + "\" as completed.";
						manager.getNotifications().add(new Notification(message, notificationData));
						employeeRepo.save(manager);
					}
				}
				scheduleRepo.save(day);
				break;
			}
		}

	}

	private void addMonthlyDays(LocalDate date) {
		YearMonth yearMonthObject = YearMonth.of(date.getYear(), date.getMonthValue());
		for (int i = 1; i <= yearMonthObject.lengthOfMonth(); i++) {
			LocalDate correctDate = date.withDayOfMonth(i);
			Day day = new Day(correctDate);
			scheduleRepo.save(day);
		}
	}

	@Scheduled(cron = "1 0 0 1 * *")
	public void applySchedule() throws IOException {

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

		// Clear next month's dates
		for (int date = 1; date <= LocalDate.now().plus(1, ChronoUnit.MONTHS).lengthOfMonth(); date++) {
			Optional<Day> day = scheduleRepo.findByDate(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(date));
			if (!day.isEmpty()) {
				scheduleRepo.deleteById(day.get().getID());
			}
		}

		// Add next month days
		addMonthlyDays(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(1));

		// Add the employee shifts
		for (int i = 97; i < 123; i++) {
			String departmentCode = Character.toString((char) i);
			JSONObject department = getDepartment(departmentCode);
			if (department == null) {
				continue;
			}
			if (Boolean.parseBoolean((String) department.get("Universal schedule"))) {
				switch ((String) department.get("Schedule type")) {
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
				for (int j = 0; j < ((JSONArray) department.get("levels")).size(); j++) {
					JSONObject employeeLevel = getDepartmentAtLevel(departmentCode, j);
					switch ((String) employeeLevel.get("Schedule type")) {
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
		List<Employee> allEmployees = employeeRepo.findAll();
		List<Object> notificationData = new ArrayList<>();
		notificationData.add("schedule-update");
		String message = "The schedule for " + YearMonth.from(LocalDate.now().plus(1, ChronoUnit.MONTHS)) + " has been updated.";
		for (Employee issuer : allEmployees) {
			issuer.getNotifications().add(new Notification(message, notificationData));
		}
	}

	@SuppressWarnings("unchecked")
	public List<EmployeeDailyReference>[] viewSchedule(Employee viewer, YearMonth month) throws IOException {
		int monthLength = LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).lengthOfMonth();
		List<EmployeeDailyReference>[] sameLevelEmployees = new ArrayList[monthLength + 1];
		for (int i = 1; i <= monthLength; i++) {
			Day thisDay = getDay(LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).withDayOfMonth(i));
			if (thisDay.getEmployees().isEmpty()) {
				continue;
			}
			if (viewer.getRole().equals("ADMIN") || viewer.getRole().equals("MANAGER")) {
				sameLevelEmployees[i] = thisDay.getEmployees();
				continue;
			} else {
				for (int j = 0; j < thisDay.getEmployees().size(); j++) {
					EmployeeDailyReference employee = thisDay.getEmployees().get(j);
					if ((employee.getDepartment().equals(viewer.getDepartment()) && employee.getLevel() <= viewer.getLevel())) {
						if (!employee.getNationalID().equals(viewer.getNationalID())) {
							if (sameLevelEmployees[i] != null) {
								sameLevelEmployees[i].add(thisDay.getEmployees().get(j));
							} else {
								List<EmployeeDailyReference> singleEDR = new ArrayList<EmployeeDailyReference>();
								singleEDR.add(thisDay.getEmployees().get(j));
								sameLevelEmployees[i] = singleEDR;
							}
						}
					}
				}
			}
		}
		return sameLevelEmployees;
	}

	@SuppressWarnings("unchecked")
	public List<Task>[] viewTasks(Employee employee, YearMonth month) throws IOException {
		int monthLength = LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).lengthOfMonth();
		List<Task>[] tasks = new ArrayList[monthLength];
		for (int i = 0; i < monthLength; i++) {
			Day taskDay = getDay(LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).withDayOfMonth(i + 1));

			EmployeeDailyReference thisEDR = null;

			edrLoop: for (EmployeeDailyReference edr : taskDay.getEmployees()) {
				if (edr.getNationalID().equals(employee.getNationalID())) {
					thisEDR = edr;
					break edrLoop;
				}
			}
			if (thisEDR != null) {
				if (thisEDR.getTasks() != null) {
					tasks[i] = thisEDR.getTasks();
				}
			}
		}
		return tasks;
	}

	public boolean applyOnCallSchedule(String department, int level) throws IOException {
		System.out.println(" ");
		JSONObject thisDepartment = getDepartmentAtLevel(department, level);
		if (thisDepartment == null)
			return false;

		List<Employee> employees = employeeRepo.findByAccessLevel(department + Integer.toString(level));
		if (employees.isEmpty()) {
			System.out.println("No employees in this department!");
			return false;
		}

		LocalDate month = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		int lengthOfMonth = YearMonth.of(month.getYear(), month.getMonth()).lengthOfMonth();

		int shiftLength;
		int breakBetweenShiftsMin;
		int employeesPerShift;
		int monthlyWorkDays;
		double lengthOfWorkDay;
		int[] dailyHours;
		try {
			dailyHours = Stream.of(((String) thisDepartment.get("Daily hours")).split(",")).mapToInt(Integer::parseInt).toArray();
			shiftLength = Integer.parseInt((String)thisDepartment.get("Shift length"));
			breakBetweenShiftsMin = Integer.parseInt((String)thisDepartment.get("Break between shifts"));
			lengthOfWorkDay = ((double) dailyHours[2] + ((double) dailyHours[3] / 60)) - ((double) dailyHours[0] + ((double) dailyHours[1] / 60));
			if (lengthOfWorkDay > 23.9) {
				lengthOfWorkDay = 24;
			}
			employeesPerShift = Integer.parseInt((String)thisDepartment.get("Employees per shift"));

			monthlyWorkDays = Integer.parseInt((String)thisDepartment.get("Monthly work days"));

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
			} while (shiftStart.isBefore(shiftEnd.plusMinutes(2))&&LocalDate.from(shiftStart) == day.getDate());
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

		System.out.println("Employees: " + employees.size());
		System.out.println("Monthly shifts: " + shifts.size());
		System.out.println("Employees per shift: " + employeesPerShift);
		System.out.println("Shifts per day: " + shiftsPerDay);
		System.out.println(" ");

		for (Employee employee : employees) {
			int initialOffset = employees.indexOf(employee) % employees.size();
			shiftLoop: for (int i = initialOffset; i < shifts.size(); i += employees.size() / employeesPerShift) {
				Day day = getDay(shifts.get(i).getDate());
				if (isLeaveDay(employee, day)) {
					continue shiftLoop;
				} else {
					addWorkDay(employee.getNationalID(), day.getDate().toString(), true, shifts.get(i).beginning.toLocalTime().toString(), shifts.get(i).end.toLocalTime().toString());
					
					System.out.println(shifts.get(i).getDate() + "  " + shifts.get(i).beginning.toLocalTime() + " - " + shifts.get(i).end.toLocalTime() + " " + "is a work shift for " + employee.getFirstName());

				}
			}
			System.out.println(" ");
		}
		return true;
	}
	
	public boolean applyIrregularSchedule(String department, int level) throws IOException {
		ArrayList<Day> month = new ArrayList<>();
		LocalDate now = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == now.getMonthValue()) {
				month.add(day);
			}
		}
		for (Employee employee : this.employeeRepo.findByAccessLevel(department + Integer.toString(level))) {
			boolean[] lastSevenDays = new boolean[7];
			LocalDate lastMonth = now.minus(1, ChronoUnit.MONTHS);
			int lengthOfPreviousMonth = YearMonth.of(now.getYear(), lastMonth.getMonth()).lengthOfMonth();
			workWeekLoop:
			// Getting the last 7 days of the previous month to properly continue the work
			// schedule
			for (LocalDate date = lastMonth.withDayOfMonth(lengthOfPreviousMonth); date.isAfter(lastMonth.withDayOfMonth(lengthOfPreviousMonth - 7)); date = date.plus(-1, ChronoUnit.DAYS)) {
				Day thisDay = getDay(date);
				for (EmployeeDailyReference edrl : thisDay.getEmployees()) {
					if (edrl.getNationalID().equals(employee.getNationalID())) {
						lastSevenDays[(date.getDayOfMonth() - (lengthOfPreviousMonth - 7)) - 1] = true;
						continue workWeekLoop;
					}
				}
			}

			boolean lookingAtWorkDays = true;
			int consecutiveBreakDays = 0;
			int consecutiveWorkDays = 0;
			for (int i = lastSevenDays.length - 1; i >= 0; i--) {
				if (lastSevenDays[i]) {
					consecutiveWorkDays++;
				} else {
					break;
				}
			}
			if (consecutiveWorkDays == 0) {
				lookingAtWorkDays = false;
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
				if (!lookingAtWorkDays) {
					if (consecutiveBreakDays < employee.getWorkWeek()[1]) {
						consecutiveBreakDays++;
						System.out.println(day.getDate() + " " + "is a break day for " + employee.getFirstName());
						continue dayLoop;
					}
				} else {
					if (consecutiveWorkDays < employee.getWorkWeek()[0]) {
						if (!isLeaveDay(employee, day)) {
							//addWorkDay(employee.getID(), day.getDate().toString(), false, null, null);
							System.out.println(day.getDate() + " " + "is a work day for " + employee.getFirstName());
						}
					}
				}

				if (isLeaveDay(employee, day)) {
					System.out.println(day.getDate() + " is a leave day for " + employee.getFirstName());
					continue dayLoop;
				} else {
					if (employeeWorkWeek[workWeekCounter]) {
						System.out.println(day.getDate() + " is a work day for " + employee.getFirstName());
						//addWorkDay(employee.getID(), day.getDate().toString(), false, null, null);
					} else {
						System.out.println(day.getDate() + " " + "is a break day for " + employee.getFirstName());
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

	public boolean applyRegularSchedule(String department, int level) throws IOException {
		ArrayList<Day> month = new ArrayList<>();
		LocalDate now = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == now.getMonthValue()) {
				month.add(day);
			}
		}

		for (Employee employee : this.employeeRepo.findByAccessLevel(department + Integer.toString(level))) {
			dayLoop: for (Day day : month) {
				if (day.getDate().getDayOfWeek() != DayOfWeek.SUNDAY && day.getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
					if (isLeaveDay(employee, day)) {
						System.out.println(day.getDate() + " is a break day for " + employee.getFirstName());
						continue dayLoop;
					} else {
						addWorkDay(employee.getNationalID(), day.getDate().toString(), false, null, null);
						System.out.println(day.getDate() + " is a work day for " + employee.getFirstName());
					}
				} else {
					System.out.println(day.getDate() + " is a break day for " + employee.getFirstName());
					continue dayLoop;
				}
			}
		}
		return true;
	}

	private boolean isLeaveDay(Employee employee, Day day) {

		if (!employee.getLeaves().isEmpty()) {
			for (Map<String, Object> leave : employee.getLeaves()) {
				try {
					if (day.getDate().isAfter((LocalDate) leave.get("Start")) && day.getDate().isBefore((LocalDate) leave.get("End"))) {
						return true;
					}
				}catch(Exception e) {
					continue;
				}
			}
		}
		return false;
	}

	private JSONObject getDepartmentAtLevel(String department, int level) {

		JSONObject thisDepartment = getDepartment(department);

		if (!Boolean.parseBoolean((String) thisDepartment.get("Universal schedule"))) {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject thisLevel = (JSONObject) levels.get(level);
			thisDepartment = (JSONObject) thisLevel.get(Integer.toString(level));
		}

		return thisDepartment;
	}

	public JSONObject getDepartment(String department) {

		JSONObject thisDepartment;
		try {
			JSONObject departments = (JSONObject) new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
			thisDepartment = (JSONObject) departments.get(department);
		} catch (Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
			return null;
		}
		return thisDepartment;
	}

	@SuppressWarnings("unchecked")
	public void setDepartment(Object departmentObj) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject department = new JSONObject((Map) departmentObj);
		JSONObject departments = (JSONObject) parser.parse(new FileReader("src/main/resources/departments.json"));

		String key = null;
		JSONObject departmentBody = null;

		Set<String> keys = department.keySet();
		Iterator<String> keyIterator = keys.iterator();
		while (keyIterator.hasNext()) {
			key = keyIterator.next();
			break;
		}
		
		Object obj = department.get(key);
		departmentBody = (JSONObject) parser.parse((String) obj);
		key = key.replace("\"", "");
		// Check if it is a new department
		if (key.equals("undefined")) {
			// Check if it is the first department
			if (departments.keySet().size() == 0) {
				key = "a";
			} else {
				// Check if the admin has specified a dpt. code
				key = ((String) departmentBody.get("departmentCode"));
				if(key!=null) {
					departmentBody.remove("departmentCode");
				} else {
					int firstCode = 97;
					Set keySet = departments.keySet();
					while (keySet.contains(String.valueOf(Character.toChars(firstCode)))) {
						firstCode++;
					}
					if (firstCode < 123) {
						key = String.valueOf(Character.toChars(firstCode)).replace("\"", "");
					} else {
						return;
					}
				}
			}
		} 

		departments.put(key, departmentBody);
		try (FileWriter file = new FileWriter("src/main/resources/departments.json")) {
			file.write(departments.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteDepartment(String departmentCode) throws IOException, ParseException {
		String key = departmentCode.replace("\"", "");
		try {
			JSONObject allDepartments = (JSONObject) new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
			allDepartments.remove(key);
			FileWriter file = new FileWriter("src/main/resources/departments.json");
			file.write(allDepartments.toJSONString());
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
