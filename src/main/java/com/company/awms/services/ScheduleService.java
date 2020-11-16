package com.company.awms.services;

import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Dictionary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;
import com.company.awms.data.schedule.Task;

@Service
public class ScheduleService {

	private static ScheduleRepo scheduleRepo;

	@Autowired
	EmployeeService employeeService;

	private JSONObject departments;

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo) {

		ScheduleService.scheduleRepo = scheduleRepo;
		
		try {
			departments = (JSONObject) new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
		} catch (Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
		}
	}

	// Call employeeService.createEmployeeDailyReference(...) in the
	// ScheduleController and them get edr from the arguments
	// And remove injection of employeeService.

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public boolean addWorkDay(String employeeID, LocalDate date, JSONObject thisDepartment, int level) {
		Employee employee;
		Day currentDay;
		LocalTime[] workTime = new LocalTime[2];
		JSONArray workTimeJSON;
		JSONObject employeeLevel = null;
		if ((boolean) thisDepartment.get("universalSchedule")) {
			workTimeJSON = (JSONArray) thisDepartment.get("dailyHours");
			employeeLevel = thisDepartment;
		} else {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject arrayElement = (JSONObject)levels.get(level);
			employeeLevel = (JSONObject) arrayElement.get(Integer.toString(level));
			workTimeJSON = (JSONArray) employeeLevel.get("dailyHours");
		}

		try {

			LocalTime start = LocalTime.of(Integer.parseInt(workTimeJSON.get(0).toString()), Integer.parseInt(workTimeJSON.get(1).toString()));
			workTime[0] = start;
			LocalTime end = start.plus(Integer.parseInt(employeeLevel.get("shiftLength").toString()) + Integer.parseInt(employeeLevel.get("dailyBreakDurationTotal").toString()), ChronoUnit.HOURS);
			workTime[1] = end;

		} catch (Exception e) {
			System.out.println("DateTime error!");
			e.printStackTrace();
			return false;
		}

		try {
			employee = EmployeeService.getRepository().findById(employeeID).get();
		} catch (Exception e) {
			System.err.println("Error finding user!");
			return false;
		}
		
		EmployeeDailyReference edr = new EmployeeDailyReference(EmployeeService.getRepository(), employee.getNationalID());
		edr.setRefFirstName(employee.getFirstName());
        edr.setRefLastName(employee.getLastName());
        edr.setDate(date);
        edr.setWorkTime(workTime);

		try {
			currentDay = scheduleRepo.findByDate(date);
		} catch (Exception e) {
			System.err.println("Date not found!");
			return false;
		}
		if (currentDay.getEmployees() != null) {
			currentDay.addEmployee(edr);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(edr);
			currentDay.setEmployees(singleEmployee);
		}
		scheduleRepo.save(currentDay);
		return true;
	}

	public boolean swapEmployees(String requestorNationalID, String receiverNationalID, String requestorDate, String receiverDate) {

		EmployeeDailyReference requestor = null;
		EmployeeDailyReference receiver = null;
		LocalDate thisRequestorDate = LocalDate.parse(requestorDate);
		LocalDate thisReceiverDate = LocalDate.parse(receiverDate);
		Day requestorDay = scheduleRepo.findByDate(thisRequestorDate.withDayOfMonth(thisRequestorDate.getDayOfMonth() + 1));
		Day receiverDay = scheduleRepo.findByDate(thisReceiverDate.withDayOfMonth(thisReceiverDate.getDayOfMonth() + 1));

		for (EmployeeDailyReference edr : requestorDay.getEmployees()) {
			if (edr.getNationalID().equals(requestorNationalID)) {
				requestor = edr;
			}
		}
		for (EmployeeDailyReference edr : receiverDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				receiver = edr;
			}
		}

		if (requestor != null && receiver != null) {
			requestorDay.getEmployees().remove(requestor);
			receiverDay.getEmployees().remove(receiver);

			LocalTime[] workTimeTemp = requestor.getWorkTime();
			requestor.setWorkTime(receiver.getWorkTime());
			receiver.setWorkTime(workTimeTemp);

			requestorDay.getEmployees().add(receiver);
			receiverDay.getEmployees().add(requestor);

			scheduleRepo.save(requestorDay);
			scheduleRepo.save(receiverDay);
			return true;
		} else {
			return false;
		}
	}

	public boolean addTask(String taskDay, String receiverNationalID) {
		Day currentDay;
		try {
			LocalDate taskDate = LocalDate.parse(taskDay);
			currentDay = getRepository().findByDate(taskDate.withDayOfMonth(taskDate.getDayOfMonth() + 1));
		} catch (Exception e) {
			System.err.println("Invalid date!");
			e.printStackTrace();
			return false;
		}
		Task task;
		for (EmployeeDailyReference edr : currentDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				task = createTask(receiverNationalID, currentDay, "Test task title", "Test task body");
				if (edr.getTasks() != null) {
					edr.getTasks().add(task);
					getRepository().save(currentDay);
					return true;
				} else {
					ArrayList<Task> taskList = new ArrayList<>();
					taskList.add(task);
					edr.setTasks(taskList);
					getRepository().save(currentDay);
					return true;
				}
			}
		}
		return false;
	}

	public Task createTask(String receiverNationalID, Day date, String taskBody, String taskTitle) {
		return new Task(receiverNationalID, date, taskBody, taskTitle);
	}

	// Get all equivalent access level employees with their schedules, by iterating
	// over dates up to a month ahead
	public ArrayList<EmployeeDailyReference> viewSchedule(String accessLevel) {
		ArrayList<EmployeeDailyReference> sameLevelEmployees = new ArrayList<>();
		for (LocalDate startDate = LocalDate.now(); startDate.isBefore(LocalDate.now().plusMonths(1)); startDate = startDate.plusDays(1)) {
			Day thisDay = scheduleRepo.findByDate(startDate);
			for (int i = 0; i < thisDay.getEmployees().size(); i++) {
				if (EmployeeService.getRepository().findByNationalID(thisDay.getEmployees().get(i).getNationalID()).getAccessLevel().equals(accessLevel)) {
					sameLevelEmployees.add(thisDay.getEmployees().get(i));
				}
			}
		}
		return sameLevelEmployees;
	}

	public boolean applyIrregularSchedule(String department, int level) {

		JSONObject thisDepartment;
		try {
			thisDepartment = (JSONObject) departments.get(department);
		} catch (Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
			return false;
		}
		if((boolean)thisDepartment.get("universalSchedule")) {
			if (!((String)thisDepartment.get("scheduleType")).equals("Irregular")) {
				System.out.println("Incorrect schedule type!");
				return false;
			}
		}else {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject thisLevel = (JSONObject)levels.get(level);
			JSONObject employeeLevel = (JSONObject) thisLevel.get(Integer.toString(level));
			if (!((String)employeeLevel.get("scheduleType")).equals("Irregular")) {
				System.out.println("Incorrect schedule type!");
				return false;
			}
		}

		ArrayList<Day> month = new ArrayList<>();
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == LocalDate.now().getMonthValue()) {
				month.add(day);
			}
		}
		for (Employee employee : EmployeeService.getRepository().findByAccessLevel(department + Integer.toString(level))) {

			boolean[] lastSevenDays = new boolean[7];
			int lengthOfPreviousMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1).getMonth()).lengthOfMonth();
			workWeekLoop: 
			//Getting the last 7 days of the previous month to properly continue the work schedule
			for (int i = lengthOfPreviousMonth; i > lengthOfPreviousMonth - 7; i--) {
				Day thisDay = scheduleRepo.findByDate(LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1).withDayOfMonth(i));
				for (EmployeeDailyReference edrl : thisDay.getEmployees()) {
					if (edrl.getRefNationalID().equals(employee.getNationalID())) {
						lastSevenDays[(i - (lengthOfPreviousMonth - 7)) - 1] = true;
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
			boolean[] employeeWorkWeek = new boolean[employee.getWorkWeek()[0]+employee.getWorkWeek()[1]];
			for(int i = 0; i < employee.getWorkWeek()[0]; i++){
				employeeWorkWeek[i] = true;
			}
			dayLoop:
			for (Day day : month) {
				if (!lookingAtWorkDays) {
					if (consecutiveBreakDays < employee.getWorkWeek()[1]) {
						consecutiveBreakDays++;
						System.out.println(day.getDate() + " " + "is a break day");
						continue dayLoop;
					}
				} else {
					if (consecutiveWorkDays < employee.getWorkWeek()[0]) {
						if(!isLeaveDay(employee, day)) {
							addWorkDay(employee.getID(), day.getDate(), thisDepartment, level);
							System.out.println(day.getDate() + " " + "is a work day");
						}
					}
				}

				if (isLeaveDay(employee, day)) {
					System.out.println(day.getDate() + " is a leave day");
					continue dayLoop;
				} else {
					if(employeeWorkWeek[workWeekCounter]) {
						System.out.println(day.getDate() + " is a work day");
						addWorkDay(employee.getID(), day.getDate(), thisDepartment, level);
					}else {
						System.out.println(day.getDate() + " " + "is a break day");
					}
					if(workWeekCounter+1 >= employeeWorkWeek.length) {
						workWeekCounter=0;
					}else {
						workWeekCounter++;
					}
					
					
				}
			}
		}
		return true;
	}

	public boolean applyRegularSchedule(String department, int level) {

		JSONObject thisDepartment;
		try {
			thisDepartment = (JSONObject) departments.get(department);
		} catch (Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
			return false;
		}

		if((boolean)thisDepartment.get("universalSchedule")) {
			if (!((String)thisDepartment.get("scheduleType")).equals("Irregular")) {
				System.out.println("Incorrect schedule type!");
				return false;
			}
		}else {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject thisLevel = (JSONObject)levels.get(level);
			JSONObject employeeLevel = (JSONObject) thisLevel.get(Integer.toString(level));
			System.out.println(levels.size());
			if (!((String)employeeLevel.get("scheduleType")).equals("Irregular")) {
				System.out.println("Incorrect schedule type!");
				return false;
			}
		}
		ArrayList<Day> month = new ArrayList<>();
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == LocalDate.now().getMonthValue()) {
				month.add(day);
			}
		}

		for (Employee employee : EmployeeService.getRepository().findByAccessLevel(department + Integer.toString(level))) {
			dayLoop: for (Day day : month) {
				if (day.getDate().getDayOfWeek() != DayOfWeek.SUNDAY && day.getDate().getDayOfWeek() != DayOfWeek.SATURDAY) {
					if (isLeaveDay(employee, day)) {
						continue dayLoop;
					} else {
						addWorkDay(employee.getID(), day.getDate(), thisDepartment, level);
					}
				} else {
					continue dayLoop;
				}
			}
		}
		return true;
	}

	private boolean isLeaveDay(Employee employee, Day day) {
		if (!employee.getLeaves().isEmpty()) {
			for (Dictionary<String, Object> leave : employee.getLeaves()) {
				if (day.getDate().isAfter((LocalDate) leave.get("Start")) && day.getDate().isBefore((LocalDate) leave.get("End"))) {
					return true;
				}
			}
		}
		return false;
	}

	public static ScheduleRepo getRepository() {
		return scheduleRepo;
	}
}
