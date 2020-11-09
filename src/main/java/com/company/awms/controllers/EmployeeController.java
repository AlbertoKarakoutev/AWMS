package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeController {
    //EmployeeService employeeService;

    @Autowired
    public EmployeeController(/*EmployeeService employeeService*/) {
        //this.employeeService = employeeService;
    }

    @PostMapping(value = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody Employee newEmployee){
        try{
            //this.employeeService.Register(newEmployee);

        } catch (Exception e){

        }

        return new ResponseEntity<String>("something", HttpStatus.OK);
    }
}
