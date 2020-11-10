package com.company.awms.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
    ScheduleService scheduleService;
    EmployeeService employeeService;
    
    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;

    }
    //Populate DB with this month's dated
    public String addDays() {
    	LocalDate now = LocalDate.now();
    	//Certain 1-28 days
    	for (int i = 1; i < 29; i++) {	
	    	LocalDate correctDate = now.withDayOfMonth(i);
	    	Day testDay = new Day(correctDate);
	    	System.out.println(correctDate);
	        ScheduleService.getRepository().save(testDay);
    	}
    	//Leap year
    	if(now.getYear()%4 == 0) {
    		//Add day 29 if leap
    		LocalDate correctDate29= now.withDayOfMonth(29);	
    		Day day29 = new Day(correctDate29);
    		System.out.println(correctDate29);
    		ScheduleService.getRepository().save(day29);
    		if(now.getMonthValue() != 2) {
    			//Add day 30 if not February
        		LocalDate correctDate30 = now.withDayOfMonth(30);
        		Day day30 = new Day(correctDate30);
        		System.out.println(correctDate30);
        		ScheduleService.getRepository().save(day30);
        	} 
    	}else {
    		//Not leap - add day29 and day 30 if not February
    		if(now.getMonthValue() != 2) {
        		LocalDate correctDate29 = now.withDayOfMonth(29);
        		Day day29 = new Day(correctDate29);
        		System.out.println(correctDate29);
        		ScheduleService.getRepository().save(day29);
        		LocalDate correctDate30 = now.withDayOfMonth(30);
        		Day day30 = new Day(correctDate30);
        		System.out.println(correctDate30);
        		ScheduleService.getRepository().save(day30);
        	}   
    	}
    	//Add day 31 if the length of month value is 31
    	if(now.getMonthValue() != 2) {
    		YearMonth yearMonthObject = YearMonth.of(now.getYear(), now.getMonthValue());
    		if(yearMonthObject.lengthOfMonth()==31) {
    			LocalDate correctDate31 = now.withDayOfMonth(31);
        		Day day31 = new Day(correctDate31);
        		System.out.println(correctDate31);
        		ScheduleService.getRepository().save(day31);
    		}
    	}    	
        return "Done";
    }
    
    @GetMapping("day/{DOM}")
    public String dayID(@PathVariable String DOM) {
    	LocalDate date = LocalDate.now();
    	LocalDate dateQuery = date.withDayOfMonth(Integer.parseInt(DOM));
    	Day day = null;
    	try {
    		day =  ScheduleService.getRepository().findByDate(dateQuery);
    	}catch(Exception e) {
    		System.err.println("Invalid date!");
    		return null;
    	}
    	return day.getID();
    }
    
    /*@Autowired
    TaskRepo taskRepo;

    @GetMapping("/schedule/task")
    public ResponseEntity<String> addTask(){
        this.taskRepo.save(new Task());
        return new ResponseEntity<>("asdads", HttpStatus.OK);
    }*/
}
