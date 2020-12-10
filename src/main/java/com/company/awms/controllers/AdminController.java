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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.employees.Employee;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.DocumentService;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@Controller
@RequestMapping("/admin")
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
    @GetMapping("/employee/all")
    public String getEmployees(Model model){
        try {
            List<Employee> employees = this.employeeService.getAllEmployees();
            model.addAttribute("employees", employees);
            
            return "employees";
        } catch (Exception e) {
            e.printStackTrace();
            return "internalServerError";
        }
    }
    @PostMapping(value = "/employee/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String registerEmployee(@RequestBody Employee newEmployee, Model model){
        try {
            this.employeeService.registerEmployee(newEmployee);
            List<Employee> employee = new ArrayList<>();
            employee.add(newEmployee);
            model.addAttribute("employee",employee);
            return "employees";
        } catch(Exception e) {
        	return "internalServerError";
        }
    }
    @GetMapping("/employee/edit/{employeeID}")
    public String editEmployee(@PathVariable String employeeID, Model model) {
    	try {
    		Employee employee = employeeService.getEmployee(employeeID);
    		model.addAttribute("employee", employee);
    		return "editEmployee";
    	}catch(Exception e) {
    		return "internalServerError";
    	}
    }
    @PutMapping("/employee/update")
    public String updateEmployeeInfo(@RequestBody Employee newEmployee, @RequestParam String employeeId, Model model){
        try {
            this.employeeService.updateEmployeeInfo(newEmployee, employeeId);
            List<Employee> employee = new ArrayList<>();
            employee.add(newEmployee);
            model.addAttribute("employee", employee);
            return "employees";
        }catch(Exception e) {
            return "internalServerError";
        }
    }
    @GetMapping("/search")
    public String searchEmployees(@RequestParam String searchTerm, @RequestParam String type, Model model) {
    	try {
    		List<Employee> employees = this.employeeService.getAllEmployees();
    		List<Employee> foundEmployees = this.employeeService.searchEmployees(employees, searchTerm, type);
    		model.addAttribute("foundEmployees", foundEmployees);
    		return "employees";
    				
    	}catch(Exception e) {
    		return "internalServerError";
    	}
    }
    
    //Schedule methods
	@PostMapping(value = "/schedule/add")
	public ResponseEntity<String> addWorkDay(@RequestParam String employeeID, @RequestParam String date, @RequestParam String startShift, @RequestParam String endShift) {
		boolean success = scheduleService.addWorkDay(employeeID, LocalDate.parse(date), true, LocalTime.parse(startShift), LocalTime.parse(endShift));
		if(success) {
			return new ResponseEntity<String>("Successfully applied schedule!", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Error applying schedule!", HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/schedule/apply")
	public ResponseEntity<String> applySchedule() {
		try{
			this.scheduleService.applySchedule();
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
    
    //Document methods
    @PostMapping(value = "/document/personal/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPersonalDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try{
            this.documentService.uploadPersonalDocument(file, employeeDetails.getID(), employeeID);

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
    @DeleteMapping(value = "/document/personal/delete/{documentID}")
    public ResponseEntity<String> deletePrivateDocument(@PathVariable int documentID, @RequestParam String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try{
            this.documentService.deletePersonalDocument(documentID, employeeDetails.getID(), ownerID);

            return new ResponseEntity<>("Successfully deleted document!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //active modules methods
    @GetMapping("/modules/get")
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
    @PostMapping("/modules/set")
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
