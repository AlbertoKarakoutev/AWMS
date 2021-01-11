package com.company.awms.services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import com.company.awms.data.schedule.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.Task;

@Service
public class SalaryService {

	private ScheduleRepo scheduleRepo;

	@Autowired
	public SalaryService(ScheduleRepo scheduleRepo) {
		this.scheduleRepo = scheduleRepo;
	}

	// Rewards for user's completed tasks
	private double getTaskRewardBonus(String nationalID) {
		double taskRewards = 0.0d;

		LocalDate date = LocalDate.now();
		List<Day> elapsedDays = getElapsedDays(date);

		for (int i = 1; i < elapsedDays.size(); i++) {
			Day thisDay = elapsedDays.get(i);
			for (EmployeeDailyReference edr : thisDay.getEmployees()) {
				if (edr.getNationalID().equals(nationalID)) {
					for(Task currentTask : edr.getTasks()) {
						if (currentTask.getCompleted() && !currentTask.getPaidFor() && currentTask.getTaskReward()!=0.0d) {
							currentTask.setPaidFor(true);
							taskRewards += currentTask.getTaskReward();
							this.scheduleRepo.save(thisDay);
						}
					}
				}
			}
		}

		return taskRewards;
	}
	
	public double estimateSalary(String nationalID, Double payPerHour) {
		double salary = 0;
		salary+=calculateWorkHours(nationalID) * payPerHour;
		salary+=getTaskRewardBonus(nationalID);

		return salary;
	}

	// Calculate work hours for this month
	public double calculateWorkHours(String nationalID) {
		double hours = 0;

		LocalDate date = LocalDate.now();
		List<Day> elapsedDays = getElapsedDays(date);

		for (int i = 1; i < elapsedDays.size(); i++) {
			Day thisDay = elapsedDays.get(i);
			for (EmployeeDailyReference edr : thisDay.getEmployees()) {
				if (edr.getNationalID().equals(nationalID)) {
					Duration shiftLength =  Duration.between(edr.getWorkTime()[0], edr.getWorkTime()[1]);
					hours += (double)shiftLength.toHours();
				}
			}
		}

		return hours;
	}

	private List<Day> getElapsedDays(LocalDate date){
		LocalDate firstDay = date.withDayOfMonth(1);
		LocalDate lastDay = date.withDayOfMonth(date.getDayOfMonth());
		return this.scheduleRepo.findAllByDateBetween(firstDay, lastDay);
	}
}
