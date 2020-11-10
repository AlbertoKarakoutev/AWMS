package com.company.awms.services;

import java.time.LocalDate;

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
        EmployeeDailyReference empDayRef = new EmployeeDailyReference(employeeRepo, employee.nationalID);
        empDayRef.setDate(date);
        empDayRef.setWorkTime(workTime);
        return empDayRef;
    }

    public void requestSwap(String requestorID, String date, String message) {
        /*Employee will be prompted and he will decide whether he wants to swap
         * with that person on that date. If he agrees, Day.swapEmployees(requesterID, this.id) will be called
         */
    }
    
    public static EmployeeRepo getRepository() {
    	return employeeRepo;
    }
    
}
