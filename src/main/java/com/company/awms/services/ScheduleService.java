package com.company.awms.services;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.schedule.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ScheduleService {
    private static ScheduleRepo scheduleRepo;

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
        }catch (Exception e) {
        	System.err.println("Error finding user!");
        	return;
        }
        EmployeeDailyReference edr = employeeService.createEmployeeDailyReference(employee, date, workTime);
        ScheduleService.scheduleRepo.findByDate(date).getEmployees().add(edr);
    }

    public void swapEmployees(String requestorID, String receiverID, String requestorDate) {
        int workTime[];
        EmployeeDailyReference requestor;
        EmployeeDailyReference receiver;

        //for(int i = 0; i < employees.size(); i++) {
            //if(employees.get(i).id.equals(requestorID)) {
                //workTime = employees.get(i).getWorkTime();
                //requestor = employees.get(i);
            //}
        //}
        //employees.remove
    }
    
    public static ScheduleRepo getRepository() {
    	return scheduleRepo;
    }
}
