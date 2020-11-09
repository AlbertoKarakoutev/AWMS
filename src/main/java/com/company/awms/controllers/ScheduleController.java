package com.company.awms.controllers;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@RestController
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    ScheduleRepo scheduleRepo;
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        
    }

    @GetMapping("day")
    public String addData() {
    	Day testDay = new Day(LocalDate.now());
        ArrayList<EmployeeDailyReference> edr = new ArrayList<EmployeeDailyReference>();
        int[] worktime = {9,15,13,30};
        Employee employee = employeeRepo.findByFirstName("Gosho").get(0);
        edr.add(employeeService.createEmployeeDailyReference(employee, LocalDate.now(), worktime));
        testDay.setEmployees(edr);
        scheduleRepo.save(testDay);
        return "Done";
    }
    
    /*@Autowired
    TaskRepo taskRepo;

    @GetMapping("/schedule/task")
    public ResponseEntity<String> addTask(){
        this.taskRepo.save(new Task());
        return new ResponseEntity<>("asdads", HttpStatus.OK);
    }*/
}
