package com.company.awms.services;

import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;

import org.json.simple.*;
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

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo) {
		ScheduleService.scheduleRepo = scheduleRepo;
	}

	// Call employeeService.createEmployeeDailyReference(...) in the ScheduleController and them get edr from the arguments
	// And remove injection of employeeService.

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public boolean addWorkDay(String employeeID, LocalDate date, int[] workTime) {
		Employee employee;
		try {
			employee = EmployeeService.getRepository().findById(employeeID).get();
		} catch (Exception e) {
			System.err.println("Error finding user!");
			return false;
		}
		EmployeeDailyReference edr = employeeService.createEmployeeDailyReference(employee, date, workTime);
		
		Day currentDay;
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

	public boolean swapEmployees(String requestorNationalID, String receiverNationalID, String requestorDate,
			String receiverDate) {

		EmployeeDailyReference requestor = null;
		EmployeeDailyReference receiver = null;
		LocalDate thisRequestorDate = LocalDate.parse(requestorDate);
		LocalDate thisReceiverDate = LocalDate.parse(receiverDate);
		Day requestorDay = scheduleRepo
				.findByDate(thisRequestorDate.withDayOfMonth(thisRequestorDate.getDayOfMonth() + 1));
		Day receiverDay = scheduleRepo
				.findByDate(thisReceiverDate.withDayOfMonth(thisReceiverDate.getDayOfMonth() + 1));

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

			int[] workTimeTemp = requestor.getWorkTime();
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

	//Get all equivalent access level employees with their schedules, by iterating over dates up to a month ahead
	public ArrayList<EmployeeDailyReference> viewSchedule(String accessLevel) {
		ArrayList<EmployeeDailyReference> sameLevelEmployees = new ArrayList<>();
		for (LocalDate startDate = LocalDate.now(); startDate
				.isBefore(LocalDate.now().plusMonths(1)); startDate = startDate.plusDays(1)) {
			Day thisDay = scheduleRepo.findByDate(startDate);
			for (int i = 0; i < thisDay.getEmployees().size(); i++) {
				if (EmployeeService.getRepository().findByNationalID(thisDay.getEmployees().get(i).getNationalID())
						.getAccessLevel().equals(accessLevel)) {
						sameLevelEmployees.add(thisDay.getEmployees().get(i));
				}
			}
		}
		return sameLevelEmployees;
	}

	public boolean applyRegularSchedule(String department, int level) {
		
		JSONObject departments;
		JSONObject thisDepartment;
		try {
			departments = (JSONObject)new JSONParser().parse(new FileReader("src/main/resources/departments.json"));
			thisDepartment = (JSONObject) departments.get(department);
		}catch(Exception e) {
			System.out.println("Could not parse JSON file!");
			e.printStackTrace();
			return false;
		}
		
		if(!((String)thisDepartment.get("scheduleType")).equals("Regular")) {
			System.out.println("Incorrect schedule type!");
			return false;
		}
		
		for(Employee employee : EmployeeService.getRepository().findByAccessLevel(department + Integer.toString(level))) {
			dayLoop:
			for(Day day : scheduleRepo.findAll()) {
				if(day.getDate().getDayOfWeek()!= DayOfWeek.SUNDAY && day.getDate().getDayOfWeek()!= DayOfWeek.SATURDAY) {
					if(!employee.getLeaves().isEmpty()) {
						for(Dictionary<String, Object> leave : employee.getLeaves()) {
							if(day.getDate().isAfter((LocalDate)leave.get("Start")) && day.getDate().isBefore((LocalDate)leave.get("End"))) {
								continue dayLoop;
							}else {
								if(Boolean.parseBoolean((String)thisDepartment.get("universalSchedule"))) {
									int[] workTime = new int[4];
									JSONArray workTimeJSON = (JSONArray) thisDepartment.get("dailyHours");
									Iterator<?> iterator = workTimeJSON.iterator();
									try {
										for(int i = 0; i < 4; i++) {
											workTime[i] = (int) iterator.next();
										}
										addWorkDay(employee.getID(), day.getDate(), workTime);				
									}catch(Exception e) {
										System.out.println("Iterator error!");
									
									}
								}
								
							}
						}
					}else {
						if((boolean)thisDepartment.get("universalSchedule")) {
							int[] workTime = new int[4];
							JSONArray workTimeJSON = (JSONArray)thisDepartment.get("dailyHours");
							Iterator<?> iterator = workTimeJSON.iterator();
							try {
								for(int i = 0; i < 4; i++) {
									long value = (long) iterator.next();
									workTime[i] = (int)value;
								}
								addWorkDay(employee.getID(), day.getDate(), workTime);				
							}catch(Exception e) {
								System.out.println(day.getDate() + " Iterator error!");
								e.printStackTrace();
								return false;
							}
						}
					}
				}else {
					continue dayLoop;
				}
			}
		}
		
		return true;
	}
	
	public static ScheduleRepo getRepository() {
		return scheduleRepo;
	}
}
