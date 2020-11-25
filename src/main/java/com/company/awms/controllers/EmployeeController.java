package com.company.awms.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping(value = "employee/{employeeId}")
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

    //only the admin can register new employee accounts
    @PostMapping(value = "employee/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestBody Employee newEmployee){
        try {
            this.employeeService.registerEmployee(newEmployee);

            return new ResponseEntity<>("Registered Successfully", HttpStatus.OK);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "employee/{employeeId}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editEmployeeInfo(@RequestBody Employee newEmployee, @PathVariable String employeeId){
        try {
            //TODO:
            //Validate that the current user trying to edit employee info is the actual employee
            this.employeeService.editEmployeeInfo(newEmployee, employeeId);

            return new ResponseEntity<>("Edited Employee", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
