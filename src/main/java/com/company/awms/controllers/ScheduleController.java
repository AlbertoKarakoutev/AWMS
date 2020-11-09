package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.schedule.Task;
import com.company.awms.data.schedule.TaskRepo;
import com.company.awms.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /*@Autowired
    TaskRepo taskRepo;

    @GetMapping("/schedule/task")
    public ResponseEntity<String> addTask(){
        this.taskRepo.save(new Task());
        return new ResponseEntity<>("asdads", HttpStatus.OK);
    }*/
}
