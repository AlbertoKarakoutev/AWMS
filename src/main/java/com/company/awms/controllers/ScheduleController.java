package com.company.awms.controllers;

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

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.schedule.Task;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	private static final boolean active = true;

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
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/decline")
	public String declineSwapRequest(Model model, @RequestParam String receiverNationalID, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String date) {
		if (!active) {
			return "notFound";
		}

		try{
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			scheduleService.declineSwap(receiverNationalID, LocalDate.parse(date));
            model.addAttribute("employee", employee);
            return "redirect:/";
		} catch(Exception e) {
			return "internalServerError";
		}
	}
	
	@PostMapping(value = "/swap")
	public String acceptSwapRequest(Model model, @RequestParam String noteNum, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String requesterNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {
		if (!active) {
			return "notFound";
		}

		try {
			String receiverNationalID = employeeService.getEmployee(employeeDetails.getID()).getNationalID();
			scheduleService.swapEmployees(requesterNationalID, receiverNationalID, requesterDate, receiverDate);
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			return "redirect:/schedule/?month="+YearMonth.now();
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping("/addTask")
	public String addTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestBody String data) {
		if(!employeeDetails.getRole().equals("MANAGER")){
			return "notAuthorized";
		}
		try {
			scheduleService.addTask(data);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "redirect:/schedule/?month="+YearMonth.now();
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@GetMapping("taskComplete")
	public String markTaskAsComplete(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails,  @RequestParam String taskNum,  @RequestParam String date) {
		try {
			scheduleService.markTaskAsComplete(employeeDetails.getID(), taskNum, date);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "redirect:/schedule/?month="+YearMonth.now();
		}catch(Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}
	
	@PostMapping("/approveTask")
	public String approveTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		try {
			this.scheduleService.approveTask(dateStr, employeeID, taskNum, employeeDetails.getID());
			
			return "redirect:/schedule/?month="+YearMonth.now();
		}catch(Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}
	
	@PostMapping("/resetTask")
	public String resetTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		try {
			this.scheduleService.resetTask(dateStr, employeeID, taskNum, employeeDetails.getID());
			
			return "redirect:/schedule/?month="+YearMonth.now();
		}catch(Exception e) {
			return "internalServerError";
		}
	}
	
	@GetMapping("")
	public String viewSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam YearMonth month) {
		if (!active) {
			return "notFound";
		}

		YearMonth monthChecked = month;
		if (!monthChecked.equals(YearMonth.now().plusMonths(1)) && !monthChecked.equals(YearMonth.now())) {
			monthChecked = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue());
		}
		try {

			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			List<EmployeeDailyReference>[][] sameLevelEmployees = this.scheduleService.viewSchedule(authenticatedEmployee, monthChecked);
			List<Task>[] tasks = this.scheduleService.viewTasks(authenticatedEmployee, monthChecked);
			model.addAttribute("sameLevelEmployees", sameLevelEmployees);
			model.addAttribute("month", monthChecked);
			model.addAttribute("tasks", tasks);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "schedule";
		} catch (IOException e) {
			e.printStackTrace();
			return "badRequest";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
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
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

	public static boolean getActive() {
		return active;
	}
}
