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

        System.out.println("After adding");
        return "neshto si";
    }

    public void addSampleData() {
        System.out.println("Adding sample data");
        employeeRepo.save(new Employee("Gosho", " Goshev", "1234567890"));
    }
}
