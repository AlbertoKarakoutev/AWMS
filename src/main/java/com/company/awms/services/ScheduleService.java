package com.company.awms.services;

import java.io.FileNotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

	private JSONObject departments;

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo, EmployeeRepo employeeRepo) {

		this.scheduleRepo = scheduleRepo;
		this.employeeRepo = employeeRepo;
	}

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public boolean addWorkDay(String employeeID, LocalDate date, boolean onCall, LocalTime startShift,
			LocalTime endShift) {
		Employee employee;
		Day currentDay;
		LocalTime[] workTime = new LocalTime[2];

		int[] workTimeJSON;
		JSONObject employeeLevel = null;

		Optional<Employee> employeeOptional = this.employeeRepo.findById(employeeID);
		if (employeeOptional.isEmpty()) {
			System.err.println("Employee Not Found!");
			return false;
		}
		employee = employeeOptional.get();

		JSONObject thisDepartment = getDepartment(employee.getDepartment());
		int level = employee.getLevel();
		if (Boolean.parseBoolean((String) thisDepartment.get("Universal schedule"))) {
			workTimeJSON = Stream.of(((String) thisDepartment.get("Daily hours")).split(","))
					.mapToInt(Integer::parseInt).toArray();
			employeeLevel = thisDepartment;
		} else {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject arrayElement = (JSONObject) levels.get(level);
			employeeLevel = (JSONObject) arrayElement.get(Integer.toString(level));
			workTimeJSON = Stream.of(((String) thisDepartment.get("Daily hours")).split(","))
					.mapToInt(Integer::parseInt).toArray();
		}

		try {
			if (!onCall) {
				LocalTime start = LocalTime.of(workTimeJSON[0], workTimeJSON[1]);
				workTime[0] = start;
				LocalTime end = start.plus(
						Integer.parseInt(employeeLevel.get("Shift length").toString())
								+ Integer.parseInt(employeeLevel.get("Daily break duration total").toString()),
						ChronoUnit.HOURS);
				workTime[1] = end;
			} else {
				workTime[0] = startShift;
				workTime[1] = endShift;
			}

		} catch (Exception e) {
			System.out.println("DateTime error!");
			e.printStackTrace();
			return false;
		}

		EmployeeDailyReference edr;
		try {
			edr = new EmployeeDailyReference(this.employeeRepo, employee.getNationalID());
			edr.setWorkTime(workTime);
		} catch (IOException e) {
			return false;
		}

		Optional<Day> dayOptional = scheduleRepo.findByDate(date);
		if (dayOptional.isEmpty()) {
			System.err.println("Invalid date!");
			return false;
		}
		currentDay = dayOptional.get();

		if (currentDay.getEmployees() != null) {
			currentDay.addEmployee(edr);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(edr);
			currentDay.setEmployees(singleEmployee);
		}

		System.out.print(currentDay.getEmployees().get(0).getWorkTimeInfo());
		// currentDay.setEmployees(new ArrayList<EmployeeDailyReference>());
		scheduleRepo.save(currentDay);
		return true;
	}

	public void declineSwap(String employeeID, LocalDate receiverDate) throws IOException {
		Optional<Employee> receiverOptional = employeeRepo.findByNationalID(employeeID);
		if (receiverOptional.isEmpty()) {
			throw new IOException();
		}
		Employee receiver = receiverOptional.get();
		List<Object> notificationData = new ArrayList<Object>();
		notificationData.add("request-reply");
		notificationData.add(receiverDate);
		String message = "Your swap request for " + receiverDate + "has been declined.";
		receiver.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(receiver);
	}

	public void swapEmployees(String requesterNationalID, String receiverNationalID, String requesterDateParam,
			String receiverDateParam) {

		EmployeeDailyReference requester = null;
		EmployeeDailyReference receiver = null;
		LocalDate requesterDate, receiverDate;
		try {
			requesterDate = LocalDate.parse(requesterDateParam);
			receiverDate = LocalDate.parse(receiverDateParam);
		} catch (Exception e) {
			System.err.println("Date not recognised!");
			return;
		}
		Optional<Day> requesterDayOptional = scheduleRepo
				.findByDate(requesterDate.withDayOfMonth(requesterDate.getDayOfMonth()));
		Optional<Day> receiverDayOptional = scheduleRepo
				.findByDate(receiverDate.withDayOfMonth(receiverDate.getDayOfMonth()));

		Day requesterDay;
		Day receiverDay;

		if (requesterDayOptional.isEmpty()) {
			System.err.println("Invalid date!");
			return;
		}
		requesterDay = requesterDayOptional.get();

		if (receiverDayOptional.isEmpty()) {
			System.err.println("Invalid date!");
			return;
		}
		receiverDay = receiverDayOptional.get();
		System.out.println(requesterNationalID);
		System.out.println(receiverNationalID);
		for (EmployeeDailyReference edr : requesterDay.getEmployees()) {
			if (edr.getNationalID().equals(requesterNationalID)) {

				requester = edr;
			}
		}
		for (EmployeeDailyReference edr : receiverDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				System.out.println(edr.getFirstName());
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

			scheduleRepo.save(requesterDay);
			scheduleRepo.save(receiverDay);

			System.out.println("Successfully swapped!");
		} else {
			System.err.println("No such EDR in those days");
		}
		List<Object> notificationData = new ArrayList<Object>();
		Employee requesterObj = employeeRepo.findByNationalID(requesterNationalID).get();
		notificationData.add("request-reply");
		notificationData.add(receiver.getNationalID());
		String message = receiver.getFirstName() + " " + receiver.getLastName()
				+ " has accepted your request to swap his/her " + receiverDate + " shift with your " + requesterDate
				+ " shift.";
		requesterObj.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(requesterObj);

	}

	public void swapRequest(String requesterID, String receiverNationalID, String requesterDateParam,
			String receiverDateParam) throws IOException {
		List<Object> notificationData = new ArrayList<Object>();

		LocalDate requesterDate, receiverDate;
		try {
			requesterDate = LocalDate.parse(requesterDateParam);
			receiverDate = LocalDate.parse(receiverDateParam);
		} catch (Exception e) {
			System.err.println("Date not recognised!");
			return;
		}

		Optional<Employee> requesterOptional = employeeRepo.findById(requesterID);
		if (requesterOptional.isEmpty()) {
			System.out.println(requesterID);
			throw new IOException("Requester not found!");
		}
		Employee requester = requesterOptional.get();

		Optional<Employee> receiverOptional = employeeRepo.findByNationalID(receiverNationalID);
		if (receiverOptional.isEmpty()) {
			throw new IOException("Requester not found!");
		}
		Employee receiver = receiverOptional.get();

		notificationData.add("swap-request");
		notificationData.add(requester.getNationalID());
		notificationData.add(requesterDate);
		notificationData.add(receiverDate);
		String message = "You have received a request from " + requester.getFirstName() + " " + requester.getLastName()
				+ " to swap his/her " + requesterDate + " shift with your " + receiverDate + " shift.";
		receiver.getNotifications().add(new Notification(message, notificationData));
		employeeRepo.save(receiver);

	}

	public void addTask(String taskDay, String receiverNationalID) {
		Day currentDay;
		LocalDate taskDate = LocalDate.parse(taskDay);
		Optional<Day> currentDayOptional = this.scheduleRepo
				.findByDate(taskDate.withDayOfMonth(taskDate.getDayOfMonth() + 1));

		if (currentDayOptional.isEmpty()) {
			System.err.println("Invalid date!");
			return;
		}
		currentDay = currentDayOptional.get();

		Task task;
		for (EmployeeDailyReference edr : currentDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				task = createTask(receiverNationalID, currentDay, "Test task title", "Test task body");
				System.out.println(currentDay);
				if (edr.getTasks() != null) {
					edr.getTasks().add(task);
					this.scheduleRepo.save(currentDay);
				} else {
					ArrayList<Task> taskList = new ArrayList<>();
					taskList.add(task);
					edr.setTasks(taskList);
					this.scheduleRepo.save(currentDay);
				}

			}
		}
	}

	public Task createTask(String receiverNationalID, Day date, String taskBody, String taskTitle) {
		return new Task(receiverNationalID, date, taskBody, taskTitle);
	}

	// Get all equivalent access level employees with their schedules, by iterating
	// over dates up to a month ahead
	@SuppressWarnings("unchecked")
	public List<EmployeeDailyReference>[] viewSchedule(Employee viewer, YearMonth month) throws IOException {
		int monthLength = LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).lengthOfMonth();
		List<EmployeeDailyReference>[] sameLevelEmployees = new ArrayList[monthLength];
		for (int i = 1; i < monthLength; i++) {
			Day thisDay;
			Optional<Day> thisDayOptional = scheduleRepo.findByDate(
					LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).withDayOfMonth(i));
			if (thisDayOptional.isEmpty()) {
				throw new IOException("Invalid date!");
			}

			thisDay = thisDayOptional.get();
			if (thisDay.getEmployees().isEmpty()) {
				continue;
			}
			if (viewer.getRole().equals("ADMIN")) {
				sameLevelEmployees[i] = thisDay.getEmployees();
				continue;
			} else {
				for (int j = 0; j < thisDay.getEmployees().size(); j++) {
					EmployeeDailyReference employee = thisDay.getEmployees().get(j);
					if ((employee.getDepartment().equals(viewer.getDepartment())
							&& employee.getLevel() <= viewer.getLevel())) {
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
			Day taskDay;
			Optional<Day> taskDayOptional = scheduleRepo.findByDate(
					LocalDate.now().withYear(month.getYear()).withMonth(month.getMonthValue()).withDayOfMonth(i + 1));
			if (taskDayOptional.isEmpty()) {
				throw new IOException("Invalid date!");
			}

			taskDay = taskDayOptional.get();

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

	@Scheduled(cron = "1 0 0 1 * *")
	public void applySchedule() {

		List<Employee> employees = employeeRepo.findAll();
		for (Employee employee : employees) {
			for (Notification notification : employee.getNotifications()) {
				if (notification.getRead()) {
					employee.getNotifications().remove(notification);
				}
			}
			employeeRepo.save(employee);
		}

		// Clear next month's dates
		for (int date = 1; date <= LocalDate.now().plus(1, ChronoUnit.MONTHS).lengthOfMonth(); date++) {
			Optional<Day> day = scheduleRepo
					.findByDate(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(date));
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
						System.out.println("regular");
						applyRegularSchedule(departmentCode, 0);
						break;
					case "Irregular":
						System.out.println("irregular");
						applyIrregularSchedule(departmentCode, 0);
						break;
					case "OnCall":
						System.out.println("oncall");
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
							System.out.println("regularlevel");
							applyRegularSchedule(departmentCode, j);
							break;
						case "Irregular":
							System.out.println("ierregularlevel" + departmentCode + j);
							applyIrregularSchedule(departmentCode, j);
							break;
						case "OnCall":
							System.out.println("oncalllevel");
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
		List<Object> notificationData = new ArrayList<Object>();
		notificationData.add("schedule-update");
		String message = "The schedule for " + YearMonth.from(LocalDate.now().plus(1, ChronoUnit.MONTHS))
				+ " has been updated.";
		for (Employee issuer : allEmployees) {
			issuer.getNotifications().add(new Notification(message, notificationData));
		}
	}

	public boolean applyOnCallSchedule(String department, int level) {
		System.out.println(" ");
		JSONObject thisDepartment = getDepartmentAtLevel(department, level);
		if (thisDepartment == null)
			return false;

		List<Employee> employees = employeeRepo.findByAccessLevel(department + Integer.toString(level));
		if (employees.isEmpty()) {
			System.out.println("No employees in this department!");
			return false;
		}

		LocalDate now = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		int lengthOfMonth = YearMonth.of(now.getYear(), now.getMonth()).lengthOfMonth();

		long shiftLength;
		long breakBetweenShiftsMin;
		long employeesPerShift;
		long monthlyWorkDays;
		double lengthOfWorkDay;
		int[] dailyHours;
		try {
			dailyHours = Stream.of(((String) thisDepartment.get("Daily hours")).split(",")).mapToInt(Integer::parseInt)
					.toArray();
			shiftLength = (long) thisDepartment.get("Shift length");
			breakBetweenShiftsMin = (long) thisDepartment.get("Break between shifts");
			lengthOfWorkDay = ((double) dailyHours[2] + ((double) dailyHours[3] / 60))
					- ((double) dailyHours[0] + ((double) dailyHours[1] / 60));
			if (lengthOfWorkDay > 23.9) {
				lengthOfWorkDay = 24;
			}
			employeesPerShift = (long) thisDepartment.get("Employees per shift");
			monthlyWorkDays = (long) thisDepartment.get("Monthly work days");

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
			if (day.getDate().getMonthValue() == now.getMonthValue()) {

				if (day.getDate().getDayOfMonth() % (lengthOfMonth / monthlyWorkDays) == 0) {
					workDays.add(day);
				}
			}
		}
		ArrayList<Shift> shifts = new ArrayList<>();
		for (Day day : workDays) {
			LocalDateTime dayHour = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours[0], dailyHours[1], 0, 0));
			LocalDateTime dayEnd = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours[2], dailyHours[3], 1, 0));
			do {
				shifts.add(new Shift(LocalDateTime.of(day.getDate(), dayHour.toLocalTime()),
						LocalDateTime.of(day.getDate(), dayHour.plusHours(shiftLength).toLocalTime())));
				dayHour = dayHour.plusHours(shiftLength);
			} while (dayHour.plusHours(shiftLength).isBefore(dayEnd));
		}

		long shiftsPerDay = (long) (lengthOfWorkDay / shiftLength);
		long employeesPerDay = employeesPerShift * shiftsPerDay;
		long breakBetweenShifts = (long) (((employees.size() - 1) * shiftLength) / employeesPerShift
				+ ((employees.size() / employeesPerShift) / shiftsPerDay) * (24 - lengthOfWorkDay));

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
			System.out.println("Employees per day required are " + employeesPerDay + ", but only " + employees.size()
					+ " can be distributed");
			return false;
		} else {
			if (breakBetweenShifts < breakBetweenShiftsMin) {
				System.out.println(
						"Break between shifts should be " + breakBetweenShiftsMin + ", but is " + breakBetweenShifts);
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
				Day day;
				try {
					day = scheduleRepo.findByDate(shifts.get(i).getDate()).get();
				} catch (Exception e) {
					break shiftLoop;
				}
				if (isLeaveDay(employee, day)) {
					continue shiftLoop;
				} else {
					// addWorkDay(employee.getID(), day.getDate(), true,
					// shifts.get(i).beginning.toLocalTime(),shifts.get(i).end.toLocalTime());

					System.out.println(shifts.get(i).getDate() + "  " + shifts.get(i).beginning.toLocalTime() + " - "
							+ shifts.get(i).end.toLocalTime() + " " + "is a work shift for " + employee.getFirstName());

				}
			}
			System.out.println(" ");
		}

		return true;
	}

	public boolean applyIrregularSchedule(String department, int level) {
		System.out.println("inalg");
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
			for (LocalDate date = lastMonth.withDayOfMonth(lengthOfPreviousMonth); date.isAfter(
					lastMonth.withDayOfMonth(lengthOfPreviousMonth - 7)); date = date.plus(-1, ChronoUnit.DAYS)) {
				Optional<Day> thisDay = scheduleRepo.findByDate(date);
				if (thisDay.isEmpty()) {
					return false;
				}
				for (EmployeeDailyReference edrl : thisDay.get().getEmployees()) {
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
							// addWorkDay(employee.getID(), day.getDate(), false, null, null);
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
						// addWorkDay(employee.getID(), day.getDate(), false, null, null);
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

	public boolean applyRegularSchedule(String department, int level) {
		ArrayList<Day> month = new ArrayList<>();
		LocalDate now = LocalDate.now().plus(1, ChronoUnit.MONTHS);
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == now.getMonthValue()) {
				month.add(day);
			}
		}

		for (Employee employee : this.employeeRepo.findByAccessLevel(department + Integer.toString(level))) {
			dayLoop: for (Day day : month) {
				if (day.getDate().getDayOfWeek() != DayOfWeek.SUNDAY
						&& day.getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
					if (isLeaveDay(employee, day)) {
						System.out.println(day.getDate() + " is a break day for " + employee.getFirstName());
						continue dayLoop;
					} else {
						// addWorkDay(employee.getID(), day.getDate(), false, null, null);
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
			departments = (JSONObject) new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
			thisDepartment = (JSONObject) departments.get(department);
		} catch (Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
			return null;
		}
		return thisDepartment;
	}

	@SuppressWarnings({ "unchecked" })
	public void setDepartment(String departmentCode, JSONObject department)
			throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		String key = departmentCode.replace("\"", "");
		JSONObject departments = (JSONObject) new JSONParser()
				.parse(new FileReader("src/main/resources/departments.json"));
		if (key.equals("undefined")) {
			for (Object keyObj : departments.keySet()) {
				String nextStr = (String) keyObj;
				char next = nextStr.replace("\"", "").charAt(0);
				int ascii = (int) next + 1;
				if (ascii < 123) {
					key = String.valueOf(Character.toChars(ascii));
				} else {
					return;
				}
			}
		}
		try {
			JSONObject departmentBody = (JSONObject) parser.parse((String) department.get(departmentCode));
			departments.put(key, departmentBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (FileWriter file = new FileWriter("src/main/resources/departments.json")) {
			file.write(departments.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isLeaveDay(Employee employee, Day day) {

		if (!employee.getLeaves().isEmpty()) {
			for (Map<String, Object> leave : employee.getLeaves()) {
				if (day.getDate().isAfter((LocalDate) leave.get("Start"))
						&& day.getDate().isBefore((LocalDate) leave.get("End"))) {
					return true;
				}
			}
		}
		return false;
	}

	public void addMonthlyDays(LocalDate date) {
		YearMonth yearMonthObject = YearMonth.of(date.getYear(), date.getMonthValue());
		for (int i = 1; i <= yearMonthObject.lengthOfMonth(); i++) {
			LocalDate correctDate = date.withDayOfMonth(i);
			Day day = new Day(correctDate);
			scheduleRepo.save(day);
		}
	}
}
