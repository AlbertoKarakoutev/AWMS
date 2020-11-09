package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @Autowired
    EmployeeRepo employeeRepo;

    //Testing connection to MongoDB cloud database
    @GetMapping("/employee/{firstName}")
    public String getByFirstName(@PathVariable String firstName){

        //addSampleData();
    	
        try {
        	System.out.println(employeeRepo.findByFirstName(firstName).get(0).info());
        	return employeeRepo.findByFirstName(firstName).get(0).info();
        }catch(Exception e) {
        	System.out.println("No such user!");
        	return "No such user!";
        }
    }

    public void addSampleData() {
        System.out.println("Adding sample data");
        employeeRepo.save(new Employee("Gosho", " Goshev", "1234567890"));
    }
}
