package com.company.awms.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.employees.Notification;

@Service
public class EmployeeService {
    private EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public Employee getEmployee(String employeeID) throws IOException{
        Optional<Employee> employee = this.employeeRepo.findById(employeeID);

        if(employee.isEmpty()){
            throw new IOException("Employee not found!");
        }

        return employee.get();
    }

    public void registerEmployee(Employee newEmployee){
        //TODO:
        //Validation? from Validator Class
        this.employeeRepo.save(newEmployee);
    }

    //can be accessed by any employee who wants to edit their info
    public void editEmployeeInfo(Employee newEmployee, String oldEmployeeID) throws IOException{
        Employee oldEmployee = getEmployee(oldEmployeeID);
        //TODO:
        //Validation? from Validator Class
        oldEmployee.setEmail(newEmployee.getEmail());
        oldEmployee.setFirstName(newEmployee.getFirstName());
        oldEmployee.setIBAN(newEmployee.getIBAN());
        oldEmployee.setLastName(newEmployee.getLastName());
        oldEmployee.setNationalID(newEmployee.getNationalID());
        oldEmployee.setPhoneNumber(newEmployee.getPhoneNumber());

        this.employeeRepo.save(oldEmployee);
    }

    // Create a reference for this employee with information about his work hours and date
    public EmployeeDailyReference createEmployeeDailyReference(Employee employee, LocalDate date, LocalTime[] workTime) throws IOException {
        EmployeeDailyReference empDayRef = new EmployeeDailyReference(employeeRepo, employee.getNationalID());
        empDayRef.setDate(date);
        empDayRef.setWorkTime(workTime);
        return empDayRef;
    }
    
    public void addNotification(String employeeID, Notification notification) throws IOException {
    	Employee employee = getEmployee(employeeID);
    	employee.getNotifications().add(notification);
    	this.employeeRepo.save(employee);
    }
    
    public void removeNotification(String employeeID, Notification notification) throws IOException {
    	Employee employee = getEmployee(employeeID);
    	try {
    		employee.getNotifications().remove(notification);
    	}catch(Exception e) {
    		System.out.println("Notification not found");
    		return;
    	}
    	this.employeeRepo.save(employee);
    }
    
    public void addLeave(String employeeID, LocalDate start, LocalDate end, boolean paid) throws IOException {
		Map<String, Object> leave = new HashMap<>();
		leave.put("Start",start);
		leave.put("End", end);
		leave.put("Paid", paid);
		Optional<Employee> employee = employeeRepo.findById(employeeID);

		if(employee.isEmpty()){
		    throw new IOException("Invalid employeeID!");
        }
		employee.get().getLeaves().add(leave);

		employeeRepo.save(employee.get());
	}
}
