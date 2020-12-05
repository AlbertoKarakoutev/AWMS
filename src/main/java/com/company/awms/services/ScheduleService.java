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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
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

	// Call employeeService.createEmployeeDailyReference(...) in the   
	// ScheduleController and them get edr from the arguments
	// And remove injection of employeeService.

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public boolean addWorkDay(String employeeID, LocalDate date, boolean onCall, LocalTime startShift, LocalTime endShift) {
		Employee employee;
		Day currentDay;
		LocalTime[] workTime = new LocalTime[2];
		
		JSONArray workTimeJSON;
		JSONObject employeeLevel = null;
		
		Optional<Employee> employeeOptional = this.employeeRepo.findById(employeeID);
		if(employeeOptional.isEmpty()){
			System.err.println("Employee Not Found!");
			return false;
		}
		employee = employeeOptional.get();
		
		JSONObject thisDepartment = getDepartment(employee.getDepartment());
		int level = employee.getLevel();
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
			if(!onCall) {
				LocalTime start = LocalTime.of(Integer.parseInt(workTimeJSON.get(0).toString()), Integer.parseInt(workTimeJSON.get(1).toString()));
				workTime[0] = start;
				LocalTime end = start.plus(Integer.parseInt(employeeLevel.get("shiftLength").toString()) + Integer.parseInt(employeeLevel.get("dailyBreakDurationTotal").toString()), ChronoUnit.HOURS);
				workTime[1] = end;
			}else {
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
			edr.setDate(date);
			edr.setWorkTime(workTime);
		} catch (IOException e){
			return false;
		}

		Optional<Day> dayOptional = scheduleRepo.findByDate(date);
		if(dayOptional.isEmpty()){
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
		scheduleRepo.save(currentDay);
		return true;
	}

	public boolean swapEmployees(String requestorNationalID, String receiverNationalID, String requestorDate, String receiverDate) {

		EmployeeDailyReference requestor = null;
		EmployeeDailyReference receiver = null;
		LocalDate thisRequestorDate = LocalDate.parse(requestorDate);
		LocalDate thisReceiverDate = LocalDate.parse(receiverDate);
		Optional<Day> requestorDayOptional = scheduleRepo.findByDate(thisRequestorDate.withDayOfMonth(thisRequestorDate.getDayOfMonth() + 1));
		Optional<Day> receiverDayOptional = scheduleRepo.findByDate(thisReceiverDate.withDayOfMonth(thisReceiverDate.getDayOfMonth() + 1));

		Day requestorDay;
		Day receiverDay;

		if(requestorDayOptional.isEmpty()){
			System.err.println("Invalid date!");
			return false;
		}
		requestorDay = requestorDayOptional.get();

		if(receiverDayOptional.isEmpty()){
			System.err.println("Invalid date!");
			return false;
		}
		receiverDay = receiverDayOptional.get();

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
		LocalDate taskDate = LocalDate.parse(taskDay);
		Optional<Day> currentDayOptional = this.scheduleRepo.findByDate(taskDate.withDayOfMonth(taskDate.getDayOfMonth() + 1));

		if(currentDayOptional.isEmpty()){
			System.err.println("Invalid date!");
			return false;
		}
		currentDay = currentDayOptional.get();

		Task task;
		for (EmployeeDailyReference edr : currentDay.getEmployees()) {
			if (edr.getNationalID().equals(receiverNationalID)) {
				task = createTask(receiverNationalID, currentDay, "Test task title", "Test task body");
				if (edr.getTasks() != null) {
					edr.getTasks().add(task);
					this.scheduleRepo.save(currentDay);
					return true;
				} else {
					ArrayList<Task> taskList = new ArrayList<>();
					taskList.add(task);
					edr.setTasks(taskList);
					this.scheduleRepo.save(currentDay);
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
	public List<List<EmployeeDailyReference>> viewSchedule(String department, int level) throws IOException {
		List<List<EmployeeDailyReference>> sameLevelEmployees = new ArrayList<>();
		for (LocalDate startDate = LocalDate.now(); startDate.isBefore(LocalDate.now().plusMonths(1)); startDate = startDate.plusDays(1)) {
			Day thisDay;
			Optional<Day> thisDayOptional = scheduleRepo.findByDate(startDate);
			if(thisDayOptional.isEmpty()){
				throw new IOException("Invalid date!");
			}
			
			thisDay = thisDayOptional.get();

			for (int i = 0; i < thisDay.getEmployees().size(); i++) {
				Optional<Employee> employeeOptional = this.employeeRepo.findByNationalID(thisDay.getEmployees().get(i).getNationalID());
				if(employeeOptional.isEmpty()){
					throw new IOException("Invalid nationalID");
				} else if(employeeOptional.get().getDepartment().equals(department) && employeeOptional.get().getLevel() == level){
					sameLevelEmployees.add(thisDay.getEmployees());
				}
			}
		}
		return sameLevelEmployees;
	}

	@Scheduled(cron = "1 0 0 1 * *")
	public void applySchedule() {
		
		//Clear next month's dates
		for(int date = 1; date <= LocalDate.now().plus(1, ChronoUnit.MONTHS).lengthOfMonth(); date++) {
			Optional<Day> day = scheduleRepo.findByDate(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(date));
			if(!day.isEmpty()) {
				scheduleRepo.deleteById(day.get().getID());
			}
		}
		
		//Add next month days
		addMonthlyDays(LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(1));
		
		//Add the employee shifts
		for(int i = 97; i < 123; i++) {
			String departmentCode = Character.toString((char)i);
			JSONObject department = getDepartment(departmentCode);
			if(department == null) {
				continue;
			}
			if((boolean)department.get("universalSchedule")) {
				switch((String)department.get("scheduleType")) {
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
			}else {
				for(int j = 0; j < ((JSONArray)department.get("levels")).size(); j++) {
					JSONObject employeeLevel = getDepartmentAtLevel(departmentCode, j);
					switch((String)employeeLevel.get("scheduleType")) {
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
	}
	
	@SuppressWarnings("unchecked")
	public boolean applyOnCallSchedule(String department, int level) {
		System.out.println(" ");
		JSONObject thisDepartment = getDepartmentAtLevel(department, level);
		if(thisDepartment == null) return false;
		
		
		List<Employee> employees = employeeRepo.findByAccessLevel(department+Integer.toString(level));
		if(employees.isEmpty()) {
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
		ArrayList<Long> dailyHours;
		try {
			dailyHours = (ArrayList<Long>)thisDepartment.get("dailyHours");
			shiftLength = (long)thisDepartment.get("shiftLength");
			breakBetweenShiftsMin = (long)thisDepartment.get("breakBetweenShifts");
			lengthOfWorkDay = ((double)dailyHours.get(2)+((double)dailyHours.get(3)/60))-((double)dailyHours.get(0)+((double)dailyHours.get(1)/60));
			if(lengthOfWorkDay>23.9){
				lengthOfWorkDay = 24;
			}
			employeesPerShift = (long) thisDepartment.get("employeesPerShift");
			monthlyWorkDays = (long) thisDepartment.get("monthlyWorkDays");
			
		}catch(Exception e) {
			System.out.println("JSON Error");
			e.printStackTrace();
			return false;
		}
		
		
		class Shift{
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
		if(lengthOfMonth/monthlyWorkDays<2)System.out.println("Shifts required are over 15, adding shifts each day");
		ArrayList<Day> workDays = new ArrayList<>();
		for (Day day : scheduleRepo.findAll()) {
			if (day.getDate().getMonthValue() == now.getMonthValue()) {
				
				if(day.getDate().getDayOfMonth()%(lengthOfMonth/monthlyWorkDays) == 0) {
					workDays.add(day);
				}
			}
		}
		ArrayList<Shift> shifts = new ArrayList<>();
		for(Day day : workDays) {
			LocalDateTime dayHour = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours.get(0).intValue(), dailyHours.get(1).intValue(), 0, 0));
			LocalDateTime dayEnd = LocalDateTime.of(day.getDate(), LocalTime.of(dailyHours.get(2).intValue(), dailyHours.get(3).intValue(), 1, 0));
			do{
				shifts.add(new Shift(LocalDateTime.of(day.getDate(), dayHour.toLocalTime()), LocalDateTime.of(day.getDate(), dayHour.plusHours(shiftLength).toLocalTime())));
				dayHour = dayHour.plusHours(shiftLength);
			}while(dayHour.plusHours(shiftLength).isBefore(dayEnd));
		}
		
		long shiftsPerDay = (long) (lengthOfWorkDay/shiftLength);
		long employeesPerDay = employeesPerShift*shiftsPerDay;
		long breakBetweenShifts =  (long) (((employees.size()-1)*shiftLength)/employeesPerShift + ((employees.size()/employeesPerShift)/shiftsPerDay)*(24-lengthOfWorkDay));
		
		if(monthlyWorkDays > lengthOfMonth) {
			System.out.println("Too many work days per month!");
			return false;
		}
		if(lengthOfWorkDay%shiftLength!=0) {
			System.out.println("Impossible to distribute shifts correctly");
			return false;
		}
		if(shifts.size()/(double)employees.size()<2.0) {
			System.out.println("Unnccessary amount of employees in this department");
			return false;
		}
		if(employeesPerDay>employees.size()) {
			System.out.println("Employees per day required are "+ employeesPerDay + ", but only " + employees.size() + " can be distributed");
			return false;
		}else {
			if(breakBetweenShifts < breakBetweenShiftsMin) {
				System.out.println("Break between shifts should be " + breakBetweenShiftsMin + ", but is " + breakBetweenShifts);
				return false;
			}
		}
		
		System.out.println("Employees: " + employees.size());
		System.out.println("Monthly shifts: " + shifts.size());
		System.out.println("Employees per shift: " + employeesPerShift);
		System.out.println("Shifts per day: " + shiftsPerDay);
		System.out.println(" ");
		
		for(Employee employee : employees) {
			int initialOffset = employees.indexOf(employee)%employees.size();
			shiftLoop:
			for(int i = initialOffset; i < shifts.size(); i+=employees.size()/employeesPerShift) {
				Day day;
				try {
					day = scheduleRepo.findByDate(shifts.get(i).getDate()).get();
				}catch(Exception e) {
					break shiftLoop;
				}
				if(isLeaveDay(employee, day)) {
					continue shiftLoop;
				}else {
					//addWorkDay(employee.getID(), day.getDate(), true, shifts.get(i).beginning.toLocalTime(),shifts.get(i).end.toLocalTime());
			
					System.out.println(shifts.get(i).getDate() + "  " + shifts.get(i).beginning.toLocalTime() + " - " + shifts.get(i).end.toLocalTime() + " " + "is a work shift for " + employee.getFirstName());

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
			//Getting the last 7 days of the previous month to properly continue the work schedule
			for(LocalDate date = lastMonth.withDayOfMonth(lengthOfPreviousMonth); date.isAfter(lastMonth.withDayOfMonth(lengthOfPreviousMonth - 7)); date = date.plus(-1, ChronoUnit.DAYS)){
				Optional<Day> thisDay = scheduleRepo.findByDate(date);
				if(thisDay.isEmpty()){
					return false;
				}
				for (EmployeeDailyReference edrl : thisDay.get().getEmployees()) {
					if (edrl.getRefNationalID().equals(employee.getNationalID())) {
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
			boolean[] employeeWorkWeek = new boolean[employee.getWorkWeek()[0]+employee.getWorkWeek()[1]];
			for(int i = 0; i < employee.getWorkWeek()[0]; i++){
				employeeWorkWeek[i] = true;
			}
			dayLoop:
			for (Day day : month) {
				if (!lookingAtWorkDays) {
					if (consecutiveBreakDays < employee.getWorkWeek()[1]) {
						consecutiveBreakDays++;
						System.out.println(day.getDate() + " " + "is a break day for " + employee.getFirstName());
						continue dayLoop;
					}
				} else {
					if (consecutiveWorkDays < employee.getWorkWeek()[0]) {
						if(!isLeaveDay(employee, day)) {
							//addWorkDay(employee.getID(), day.getDate(), false, null, null);
							System.out.println(day.getDate() + " " + "is a work day for " + employee.getFirstName());
						}
					}
				}

				if (isLeaveDay(employee, day)) {
					System.out.println(day.getDate() + " is a leave day for " + employee.getFirstName());
					continue dayLoop;
				} else {
					if(employeeWorkWeek[workWeekCounter]) {
						System.out.println(day.getDate() + " is a work day for " + employee.getFirstName());
						//addWorkDay(employee.getID(), day.getDate(), false, null, null);
					}else {
						System.out.println(day.getDate() + " " + "is a break day for " + employee.getFirstName());
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
						//addWorkDay(employee.getID(), day.getDate(), false, null, null);
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

	private JSONObject getDepartmentAtLevel(String department,int level) {
		
		JSONObject thisDepartment = getDepartment(department);

		if(!(boolean)thisDepartment.get("universalSchedule")) {
			JSONArray levels = (JSONArray) thisDepartment.get("levels");
			JSONObject thisLevel = (JSONObject)levels.get(level);
			thisDepartment = (JSONObject) thisLevel.get(Integer.toString(level));
		}
		
		return thisDepartment;
	}
	
	private JSONObject getDepartment(String department) {
		
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
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void setDepartmentField(String department, String field, String value) {
		
		JSONObject thisDepartment = getDepartment(department);

		thisDepartment.put(field, value);
		departments.put(department, thisDepartment);
		
		 try (FileWriter file = new FileWriter("src/main/resources/departments.json")) {
	            file.write(departments.toJSONString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	private boolean isLeaveDay(Employee employee, Day day) {

		if (!employee.getLeaves().isEmpty()) {
			for (Map<String, Object> leave : employee.getLeaves()) {
				if (day.getDate().isAfter((LocalDate) leave.get("Start")) && day.getDate().isBefore((LocalDate) leave.get("End"))) {
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
