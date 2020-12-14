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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public void swapRequest(@AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String receiverNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {
		try {
			scheduleService.swapRequest(employeeDetails.getID(),receiverNationalID, requesterDate, receiverDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/decline")
	public String declineSwap(Model model, @RequestParam String receiverNationalID, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String date) {
		try{
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			scheduleService.declineSwap(receiverNationalID, LocalDate.parse(date));
            model.addAttribute("employee", employee);
            return "redirect:/";
		}catch(Exception e) {
			return "internalServerError";
		}
	}
	
	@PostMapping(value = "/swap")
	public String swapEmployees(Model model, @RequestParam String noteNum, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String requesterNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {
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

	@ResponseBody
	@GetMapping("/task/add")
	public ResponseEntity<String> addTask(@RequestParam String taskDay, @RequestParam String receiverNationalID) {
		try {
			scheduleService.addTask(taskDay, receiverNationalID);
			return new ResponseEntity<>("Added task for " + receiverNationalID, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error adding task for " + receiverNationalID, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("")
	public String viewSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam YearMonth month) {
		YearMonth monthChecked = month;
		if (!monthChecked.equals(YearMonth.now().plusMonths(1)) && !monthChecked.equals(YearMonth.now())) {
			monthChecked = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue());
		}
		try {

			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			List<EmployeeDailyReference>[] sameLevelEmployees = this.scheduleService.viewSchedule(authenticatedEmployee, monthChecked);
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

	@GetMapping("/dismiss")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum) {
		try{
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			return "redirect:/";
		}catch(Exception e) {
			return "internalServerError";
		}
	}
	
	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
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
