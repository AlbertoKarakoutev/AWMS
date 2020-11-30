package com.company.awms.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.employees.Employee;
import com.company.awms.services.DocumentService;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@RestController
public class AdminController {
	
    private EmployeeService employeeService;
    private DocumentService documentService;
	private ScheduleService scheduleService;
    
    @Autowired
    public AdminController(EmployeeService employeeService, DocumentService documentService,ScheduleService scheduleService) {
    	this.documentService = documentService;
        this.employeeService = employeeService;
        this.scheduleService = scheduleService;
    }

    //Employee methods
    @GetMapping("/admin/employee")
    public ResponseEntity<Employee> getEmployee(@RequestParam String employeeId){
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
    @PostMapping(value = "/admin/employee/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestBody Employee newEmployee){
        try {
            this.employeeService.registerEmployee(newEmployee);

            return new ResponseEntity<>("Registered Successfully", HttpStatus.OK);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value = "/admin/employee/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editEmployeeInfo(@RequestBody Employee newEmployee, @RequestParam String employeeId){
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
    
    //Schedule methods
	@PostMapping(value = "/admin/schedule/add")
	public ResponseEntity<String> addWorkDay(@RequestParam String employeeID, @RequestParam LocalDate date, @RequestParam LocalTime startShift, @RequestParam LocalTime endShift) {
		boolean success = scheduleService.addWorkDay(employeeID, date, true, startShift, endShift);
		if(success) {
			return new ResponseEntity<String>("Successfully applied schedule!", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Error applying scheduole!", HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/admin/schedule/apply")
	public ResponseEntity<String> applySchedule() {
		try{
			this.scheduleService.applySchedule();
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
    
    //Document methods
    @PostMapping(value = "/admin/document/personal/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPersonalDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @RequestParam String uploaderID){
        try{
            //uploaderID can be taken from the authentication - only admin can upload to employeeID's private documents
            this.documentService.uploadPersonalDocument(file, uploaderID, employeeID);

            return new ResponseEntity<>("Successfully uploaded document!", HttpStatus.OK);
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(value = "/admin/document/personal/delete/{documentID}")
    public ResponseEntity<String> deletePrivateDocument(@PathVariable int documentID, @RequestParam String employeeID, @RequestParam String ownerID){
        try{
            //employeeID can be taken from the authentication

            this.documentService.deletePersonalDocument(documentID, employeeID, ownerID);

            return new ResponseEntity<>("Successfully deleted document!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/modules/get")
    public ResponseEntity<Map<String, Boolean>> getActives(){
    	File controllerDir = new File("target/classes/com/company/awms/controllers");
    	File[] controllers = controllerDir.listFiles();
    	Map<String, Boolean> controllerConditions = new HashMap<>();
    	for(File controllerFile : controllers){	
    		String controllerName = controllerFile.getName().split("\\.")[0];  		
    		try {
    			if(!controllerName.equals("AdminController")&&!controllerName.equals("IndexController")) {
    				Class<?> controller = Class.forName("com.company.awms.controllers."+controllerName);
    				Method active = controller.getMethod("getActive");
    				controllerConditions.put(controllerName, (boolean) active.invoke(null, null));
    			}
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return new ResponseEntity<Map<String, Boolean>>(controllerConditions, HttpStatus.OK);	
    }
    
    @PostMapping("/admin/modules/set")
    public ResponseEntity<String> setActives(@RequestParam Map<String, Boolean> actives){
    	for(String key : actives.keySet()) { 		
    		try {
    			Class<?> controller = Class.forName("com.company.awms.controllers."+key);
    			Method active = controller.getMethod("setActive");
    			active.invoke(null, actives.get(key));
    		}catch(Exception e) {
    		}
    	}
    	return new ResponseEntity<>("Completed", HttpStatus.OK);	
    }
    
}
