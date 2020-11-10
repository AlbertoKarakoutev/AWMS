package com.company.awms.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.Task;

@Service
public class SalaryService {

	@Autowired
	public SalaryService() {
	}

	// Rewards for user's completed tasks
	public double taskRewardBonus(String nationalID) {
		double taskRewards = 0;	
		LocalDate date = LocalDate.now();
		for (int i = 1; i < date.getDayOfMonth(); i++) {
			LocalDate dateTemplate = date.withDayOfMonth(i);
			Day thisDay = ScheduleService.getRepository().findByDate(dateTemplate);
			for (EmployeeDailyReference edr : thisDay.getEmployees()) {
				if (edr.getNationalID().equals(nationalID)) {
					for(Task currentTask : edr.getTasks()) {	
						if (currentTask.getCompleted() && !currentTask.getPaidFor() && currentTask.getTaskReward()!=0.0) {
							currentTask.setPaidFor(true);
							taskRewards+=currentTask.getTaskReward();
							ScheduleService.getRepository().save(thisDay);
						}
					}
				}
			}
		}
		return taskRewards;
	}
	
	public double estimateSalary(String nationalID, Double payPerHour) {
		double salary = 0;
		salary+=calculateWorkHours(nationalID)*payPerHour;
		salary+=taskRewardBonus(nationalID);	
		return salary;
	}

	// Calculate work hours for this month
	public double calculateWorkHours(String nationalID) {
		double hours = 0;

		LocalDate date = LocalDate.now();
		try {
			
			for (int i = 1; i < date.getDayOfMonth(); i++) {
				LocalDate dateTemplate = date.withDayOfMonth(i);
				Day thisDay = ScheduleService.getRepository().findByDate(dateTemplate);
				for (EmployeeDailyReference edr : thisDay.getEmployees()) {
					if (edr.getNationalID().equals(nationalID)) {
						hours += (edr.getWorkTime()[2] + (edr.getWorkTime()[3] / 60))
								- (edr.getWorkTime()[0] + (edr.getWorkTime()[1] / 60));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hours;
	}

}
