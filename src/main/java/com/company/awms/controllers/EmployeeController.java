package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.awms.data.employees.Employee;
import com.company.awms.services.EmployeeService;

import java.io.IOException;

@RestController
public class EmployeeController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(value = "employee/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> getEmployee(@PathVariable String employeeId){
        try {
            Employee employee = this.employeeService.getEmployee(employeeId);

            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "employee/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestBody Employee newEmployee){
        try {
            //Validate that the current user trying to register a new employee is the Admin
            this.employeeService.registerEmployee(newEmployee);

            return new ResponseEntity<>("Registered Successfully", HttpStatus.OK);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "employee/{employeeId}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editEmployeeInfo(@RequestBody Employee newEmployee, @PathVariable String employeeId){
        try {
            //Validate that the current user trying to register a new employee is the Admin
            this.employeeService.editEmployeeInfo(newEmployee, employeeId);

            return new ResponseEntity<>("Edited Employee", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
