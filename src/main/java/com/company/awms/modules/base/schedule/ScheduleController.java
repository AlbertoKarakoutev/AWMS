package com.company.awms.modules.base.schedule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeDailyReference;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.schedule.data.Task;
import com.company.awms.security.EmployeeDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	private ScheduleService scheduleService;
	private EmployeeService employeeService;

	@Autowired
	public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService) {
		this.scheduleService = scheduleService;
		this.employeeService = employeeService;
	}

	@GetMapping("/swapRequest")
	public ResponseEntity<String> swapRequest(@AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String receiverNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {
		try {
			scheduleService.swapRequest(employeeDetails.getID(),receiverNationalID, requesterDate, receiverDate);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch(IOException e1) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			if(e.getMessage().equals("The requester already has a shift in that day!")) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/decline")
	public String declineSwapRequest(Model model, @RequestParam String receiverNationalID, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String date) {

		try{
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
			Notification.setAsRead(employeeService, employeeDetails.getID(), Integer.parseInt(noteNum));
			scheduleService.declineSwap(receiverNationalID, LocalDate.parse(date));
            model.addAttribute("employee", employee);
            return "redirect:/";
		} catch(Exception e) {
			return "erorrs/internalServerError";
		}
	}
	
	@PostMapping(value = "/swap")
	public String acceptSwapRequest(Model model, @RequestParam String noteNum, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String requesterNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {

		try {
			String receiverNationalID = employeeService.getEmployee(employeeDetails.getID()).getNationalID();
			scheduleService.swapEmployees(requesterNationalID, receiverNationalID, requesterDate, receiverDate);
			Notification.setAsRead(employeeService, employeeDetails.getID(), Integer.parseInt(noteNum));
			return "redirect:/schedule/?month="+YearMonth.now();
		} catch (Exception e) {
			e.printStackTrace();
			return "erorrs/internalServerError";
		}
	}

	@PostMapping("/addTask")
	public ResponseEntity<String> addTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestBody String data) {
		if(!employeeDetails.getRole().equals("MANAGER")){
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		}
		try {
			scheduleService.addTask(data);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("taskComplete")
	public ResponseEntity<String> markTaskAsComplete(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails,  @RequestParam String taskNum,  @RequestParam String date) {
		try {
			scheduleService.markTaskAsComplete(employeeDetails.getID(), taskNum, date);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return new ResponseEntity<String>(HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/approveTask")
	public String approveTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		try {
			this.scheduleService.approveTask(dateStr, employeeID, taskNum, employeeDetails.getID());
			
			return "redirect:/schedule/?month="+YearMonth.now();
		}catch(Exception e) {
			e.printStackTrace();
			return "erorrs/internalServerError";
		}
	}
	
	@PostMapping("/resetTask")
	public String resetTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		try {
			this.scheduleService.resetTask(dateStr, employeeID, taskNum, employeeDetails.getID());
			
			return "redirect:/schedule/?month="+YearMonth.now();
		}catch(Exception e) {
			return "erorrs/internalServerError";
		}
	}
	
	@GetMapping("")
	public String getSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam YearMonth month) {

		YearMonth monthChecked = month;
		if (!monthChecked.equals(YearMonth.now().plusMonths(1)) && !monthChecked.equals(YearMonth.now())) {
			monthChecked = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue());
		}
		try {

			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			boolean[] employeeWorkDays = this.scheduleService.getSchedule(authenticatedEmployee, monthChecked);
			model.addAttribute("employeeWorkDays", employeeWorkDays);
			model.addAttribute("month", monthChecked);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/schedule/schedule";
		} catch (IOException e) {
			e.printStackTrace();
			return "erorrs/badRequest";
		} catch (Exception e) {
			e.printStackTrace();
			return "erorrs/internalServerError";
		}
	}
	
	@GetMapping("/day")
	public ResponseEntity<String> getDay(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr){
		try {
			 injectLoggedInEmployeeInfo(model, employeeDetails);
			 LocalDate date = LocalDate.parse(dateStr);
			 ObjectMapper mapper = new ObjectMapper();
			 String role = " { \"role\" : \"" + employeeDetails.getRole() + "\" , \"employees\" : ";
			 List<EmployeeDailyReference> day = scheduleService.getDailySchedule(date, employeeDetails.getID());
			 boolean workDayForEmployee = false;
			 for(EmployeeDailyReference edr : day) {
				 if(edr.getNationalID().equals(employeeDetails.getNationalID())){
					 workDayForEmployee = true;
					 break;
				 }
			 }
			 return new ResponseEntity<String>(role +mapper.writeValueAsString(day) + ", \"workDayForEmployee\": "+workDayForEmployee+"}", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/day/tasks")
	public ResponseEntity<String> getDayTasks(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr){
		try {
			 injectLoggedInEmployeeInfo(model, employeeDetails);
			 LocalDate date = LocalDate.parse(dateStr);
			 ObjectMapper mapper = new ObjectMapper();
			 List<Task> dayTasks = scheduleService.getDailyTasks(date, employeeDetails.getID());
			 return new ResponseEntity<String>(mapper.writeValueAsString(dayTasks), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/upcoming")
	public ResponseEntity<String> viewScheduleAfterDate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr, @RequestParam String receiverNationalID){
		try {
			 injectLoggedInEmployeeInfo(model, employeeDetails);
			 ObjectMapper mapper = new ObjectMapper();
			 List<String> schedule = scheduleService.getScheduleAfterDate(employeeDetails.getID(), dateStr, receiverNationalID);
			 return new ResponseEntity<String>(mapper.writeValueAsString(schedule), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		model.addAttribute("employeeNationalID", employeeDetails.getNationalID());
		Employee user = employeeService.getEmployee(employeeDetails.getID());
		int unread = 0;
		for(int i = 0; i < user.getNotifications().size(); i++) {
			if(!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		model.addAttribute("extModules", employeeService.getExtensionModulesDTOs());
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

}
