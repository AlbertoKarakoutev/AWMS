package com.company.awms.services;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;

@Service
public class ScheduleService {

	private static ScheduleRepo scheduleRepo;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	public ScheduleService(ScheduleRepo scheduleRepo) {
		ScheduleService.scheduleRepo = scheduleRepo;
	}

	// Create a employee reference with appropriate information and add to the
	// current day employees array
	public void addEmployee(String nationalID, LocalDate date, int[] workTime) {
		Employee employee;
		try {
			employee = EmployeeService.getRepository().findByNationalID(nationalID);
		} catch (Exception e) {
			System.err.println("Error finding user!");
			return;
		}
		EmployeeDailyReference edr = employeeService.createEmployeeDailyReference(employee, date, workTime);
		Day currentDay;
		try {
			currentDay = scheduleRepo.findByDate(date);
		}catch(Exception e) {
			System.err.println("Date not dound!");
			return;
		}
		if (currentDay.getEmployees() != null) {
			scheduleRepo.findByDate(date).getEmployees().add(edr);
		} else {
			ArrayList<EmployeeDailyReference> singleEmployee = new ArrayList<>();
			singleEmployee.add(edr);
			currentDay.setEmployees(singleEmployee);
			System.out.println(edr.refFirstName);
		}
		scheduleRepo.save(currentDay);
	}

	public void swapEmployees(String requestorID, String receiverID, String requestorDate) {
//		int workTime[];
//		EmployeeDailyReference requestor;
//		EmployeeDailyReference receiver;

//		 for(int i = 0; i < employees.size(); i++) {
//		 if(employees.get(i).id.equals(requestorID)) {
//		 workTime = employees.get(i).getWorkTime();
//		 requestor = employees.get(i);
//		 }
//		 }
//		 employees.remove
	}

	public static ScheduleRepo getRepository() {
		return scheduleRepo;
	}
}
