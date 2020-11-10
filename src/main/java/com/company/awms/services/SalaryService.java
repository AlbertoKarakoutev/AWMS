package com.company.awms.services;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;
import com.company.awms.data.schedule.Task;
import com.company.awms.data.schedule.TaskRepo;

@Service
public class SalaryService {

	
	@Autowired
	public SalaryService() {
	}
	
	//Rewards for specific tasks in a user's rewards array
	public boolean rewardBonus(String userID, int reward, Task task){
		if(task.getCompleted() && !task.getPaidFor()) {
			try {
				Employee rewarded = EmployeeService.getRepository().findById(userID).get();
				rewarded.addReward(reward);
			}catch(Exception e) {
				System.out.println("User not found!");
				e.printStackTrace();
				return false;
			}
			task.setPaidFor(true);
		}
		return true;
		
	}
	
	//Calculate work hours in the past 30 days
	public double calculateWorkHours(String id) {
		double hours = 0;
		Employee worker;
		try {
			worker =  EmployeeService.getRepository().findByLastName(id).get(0); 
			
		}catch(Exception e) {
			System.out.println("User not found!");
			e.printStackTrace();
			return 0;
		}

		LocalDate date = LocalDate.now();
		try {
			for(int i = 1; i < date.getDayOfMonth(); i++) {
				LocalDate dateTemplate = date.withDayOfMonth(i);
				System.out.println(dateTemplate);
				Day thisDay = ScheduleService.getRepository().findByDate(dateTemplate);
				for(EmployeeDailyReference edr : thisDay.getEmployees()) {
					if(edr.getID().equals(id)) {
						hours+=(edr.getWorkTime()[2]+(edr.getWorkTime()[3]/60))-(edr.getWorkTime()[0]+(edr.getWorkTime()[1]/60));
					}
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return hours;
	}

}
