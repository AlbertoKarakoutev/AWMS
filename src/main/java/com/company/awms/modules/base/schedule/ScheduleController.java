package com.company.awms.modules.base.schedule;

import java.io.IOException;
import java.time.YearMonth;

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
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.security.EmployeeDetails;

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

	@GetMapping("/decline")
	public String declineSwapRequest(Model model, @RequestParam String receiverNationalID, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam int noteNum, @RequestParam String date) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			scheduleService.declineSwap(receiverNationalID, date);
			Notification.setAsRead(employeeService, employeeDetails.getID(), noteNum);
			return "redirect:/schedule/?month=" + YearMonth.now();
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/swap")
	public String acceptSwapRequest(Model model, @RequestParam String noteNum, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String requesterNationalID, @RequestParam String requesterDate,
			@RequestParam String receiverDate) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			scheduleService.swapEmployees(requesterNationalID, employeeDetails.getID(), requesterDate, receiverDate);
			Notification.setAsRead(employeeService, employeeDetails.getID(), Integer.parseInt(noteNum));
			return "redirect:/schedule/?month=" + YearMonth.now();
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping("/approveTask")
	public String approveTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			scheduleService.approveTask(dateStr, employeeID, taskNum, employeeDetails.getID());
			return "redirect:/schedule/?month=" + YearMonth.now();
		} catch (IOException e) {
			return "errors/notFound";
		} catch (IllegalAccessException e) {
			return "errors/notAuthorized";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping("/resetTask")
	public String resetTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String dateStr, @RequestParam String taskNum, @RequestParam String employeeID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			scheduleService.resetTask(dateStr, employeeID, taskNum, employeeDetails.getID());

			return "redirect:/schedule/?month=" + YearMonth.now();
		} catch (IOException e) {
			return "errors/notFound";
		} catch (IllegalAccessException e) {
			return "errors/notAuthorized";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("")
	public String getSchedule(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam YearMonth month) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		YearMonth monthChecked = month;
		if (!monthChecked.equals(YearMonth.now().plusMonths(1)) && !monthChecked.equals(YearMonth.now())) {
			monthChecked = YearMonth.of(YearMonth.now().getYear(), YearMonth.now().getMonthValue());
		}
		try {

			Employee authenticatedEmployee = this.employeeService.getEmployee(employeeDetails.getID());
			boolean[] employeeWorkDays = this.scheduleService.getSchedule(authenticatedEmployee, monthChecked);
			model.addAttribute("employeeWorkDays", employeeWorkDays);
			model.addAttribute("month", monthChecked);
			
			return "base/schedule/schedule";
		} catch (IOException e) {
			return "errors/badRequest";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/swapRequest")
	public ResponseEntity<String> swapRequest(@AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String receiverNationalID, @RequestParam String requesterDate, @RequestParam String receiverDate) {
		try {
			scheduleService.swapRequest(employeeDetails.getID(), receiverNationalID, requesterDate, receiverDate);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			if (e.getMessage().equals("The requester already has a shift in that day!")) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/addTask")
	public ResponseEntity<String> addTask(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestBody String data) {
		if (!employeeDetails.getRole().equals("MANAGER")) {
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		}
		try {
			scheduleService.addTask(data);
			employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("taskComplete")
	public ResponseEntity<String> markTaskAsComplete(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String taskNum, @RequestParam String date) {
		try {
			scheduleService.markTaskAsComplete(employeeDetails.getID(), taskNum, date);
			employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/employee/get")
	public ResponseEntity<String> getEmployeeScheduleAsString(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String employeeNationalID) {
		try {
			String employeeSchedule = scheduleService.getEmployeeScheduleAsString(employeeNationalID, employeeDetails.getID());
			return new ResponseEntity<String>(employeeSchedule, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/day")
	public ResponseEntity<String> getDayAsString(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr) {
		try {
			employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
			String dailySchedule = scheduleService.getDailySchedule(dateStr, employeeDetails);
			return new ResponseEntity<String>(dailySchedule, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/day/tasks")
	public ResponseEntity<String> getDailyTasks(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr) {
		try {
			employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
			String dayTasks = scheduleService.getDailyTasks(dateStr, employeeDetails.getID());
			return new ResponseEntity<String>(dayTasks, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/upcoming")
	public ResponseEntity<String> getScheduleAfterDate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String dateStr, @RequestParam String receiverNationalID) {
		try {
			employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);

			String schedule = scheduleService.getScheduleAfterDate(employeeDetails.getID(), dateStr, receiverNationalID);
			return new ResponseEntity<String>(schedule, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
