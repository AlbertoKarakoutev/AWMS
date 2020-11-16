package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.data.employees.Employee;
import com.company.awms.services.EmployeeService;

@RestController
public class EmployeeController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody Employee newEmployee){
        try {
            //this.employeeService.Register(newEmployee);

        	//return employeeRepo.findByFirstName(firstName).get(0).info();
        } catch(Exception e) {
        	System.out.println("No such user!");
        	return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Registered Successfully", HttpStatus.OK);
    }

    //To be used after implementing EmployeeService
    /*@GetMapping(value = "employee/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee getEmployee(@PathVariable String username){


        try {
            this.employeeService.findByUsername(username);

            Employee employee = employeeService.findByUsername(username);
            System.out.println(employee.info());
        } catch(Exception e) {
            System.out.println("No such user!");
            return  new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }

        return employee;
    }*/

    //Testing connection to MongoDB cloud database
    @GetMapping("/employee/{firstName}")
    public ResponseEntity<Employee> getByFirstName(@PathVariable String firstName){
        //addSampleData();
        try {
            Employee employee = EmployeeService.getRepository().findByFirstName(firstName).get(0);
            System.out.println(employee.info());
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch(Exception e) {
            System.out.println("No such user!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
