package com.company.awms.services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
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
	private double getTaskRewardBonus(Employee employee) {
		double taskRewards = 0.0d;

		LocalDate date = LocalDate.now();
		List<Day> elapsedDays = getElapsedDays(date);

		for (int i = 0; i < elapsedDays.size(); i++) {
			Day thisDay = elapsedDays.get(i);
			for (EmployeeDailyReference edr : thisDay.getEmployees()) {
				if (edr.getNationalID().equals(employee.getNationalID())) {
					for(Task currentTask : edr.getTasks()) {
						if (currentTask.getCompleted() &&  currentTask.getTaskReward()!=0.0d) {
							taskRewards += currentTask.getTaskReward();

							if(!currentTask.getPaidFor()){
								currentTask.setPaidFor(true);
								this.scheduleRepo.save(thisDay);
							}
						}
					}
				}
			}
		}

		return taskRewards;
	}
	
	public double estimateSalary(Employee employee) {
		double salary = 0;
		salary+=calculateWorkHours(employee) * employee.getPayPerHour();
		salary+=getTaskRewardBonus(employee);

		return salary;
	}

	// Calculate work hours for this month
	public double calculateWorkHours(Employee employee) {
		double hours = 0;

		LocalDate date = LocalDate.now();
		List<Day> elapsedDays = getElapsedDays(date);

		for (int i = 0; i < elapsedDays.size(); i++) {
			Day thisDay = elapsedDays.get(i);
			for (EmployeeDailyReference edr : thisDay.getEmployees()) {
				if (edr.getNationalID().equals(employee.getNationalID())) {
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
		return this.scheduleRepo.findAllByDateBetween(firstDay.minusDays(1), lastDay.plusDays(1));
	}
}
