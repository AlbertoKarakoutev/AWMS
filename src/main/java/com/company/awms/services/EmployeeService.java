package com.company.awms.services;

import java.time.LocalDate;
import java.util.Dictionary;
import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;

@Service
public class EmployeeService {
    private static EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        EmployeeService.employeeRepo = employeeRepo;
    }

    // Create a reference for this employee with information about his work hours and date
    public EmployeeDailyReference createEmployeeDailyReference(Employee employee, LocalDate date, int[] workTime) {
        EmployeeDailyReference empDayRef = new EmployeeDailyReference(employeeRepo, employee.getNationalID());
        empDayRef.setRefFirstName(employee.getFirstName());
        empDayRef.setRefLastName(employee.getLastName());
        empDayRef.setDate(date);
        empDayRef.setWorkTime(workTime);
        return empDayRef;
    }

    public void requestSwap(String requestorID, String date, String message) {
        /*Employee will be prompted and he will decide whether he wants to swap
         * with that person on that date. If he agrees, Day.swapEmployees(requesterID, this.id) will be called
         */
    }
    
    public void addLeave(String employeeID, LocalDate start, LocalDate end, boolean paid) {
		Dictionary<String, Object> leave = new Hashtable<String, Object>();
		leave.put("Start",start);
		leave.put("End", end);
		leave.put("Paid", paid);
		Employee employee = null;
		try {
			employee = employeeRepo.findById(employeeID).get();
			employee.getLeaves().add(leave);
		}catch(Exception e) {
			System.out.println("Cannot find user!");
		}
		if(employee!=null) {
			employeeRepo.save(employee);
		}else {
			System.out.println("Error!");
		}
	}
	
    
    public static EmployeeRepo getRepository() {
    	return employeeRepo;
    }
    
}
