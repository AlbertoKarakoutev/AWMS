package com.company.awms.services;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class EmployeeService {
    private EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    // Create a reference for this employee with information about his work hours and date
    public EmployeeDailyReference createEmployeeDailyReference(Employee employee, LocalDate date, int[] workTime) {
        EmployeeDailyReference empDayRef = new EmployeeDailyReference(employee.nationalID);
        empDayRef.setDate(date);
        empDayRef.setWorkTime(workTime);
        return empDayRef;
    }

    public void requestSwap(String requesterID, String date, String message) {
        /*Employee will be prompted and he will decide whether he wants to swap
         * with that person on that date. If he agrees, Day.swapEmployees(requesterID, this.id) will be called
         */
    }
}
