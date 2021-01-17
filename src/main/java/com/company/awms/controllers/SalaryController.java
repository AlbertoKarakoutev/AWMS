package com.company.awms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.company.awms.data.employees.Employee;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.SalaryService;

@Controller
@RequestMapping("/salary")
public class SalaryController {

	private static final double PAY_PER_HOUR = 6.5d;
	private static boolean active = true;

	private SalaryService salaryService;
	private EmployeeService employeeService;

	@Autowired
	public SalaryController(SalaryService salaryService, EmployeeService employeeService) {
		this.salaryService = salaryService;
		this.employeeService = employeeService;
	}

	@GetMapping
	public String getSalary(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		if (!active) {
			return "notFound";
		}

		try {
			Employee employee = employeeService.getEmployee(employeeDetails.getID());

			double workHours = this.salaryService.calculateWorkHours(employee);
			double salary = salaryService.estimateSalary(employee);
			double taskRewards = salary - workHours * employee.getPayPerHour();

			this.employeeService.updateSalary(salary, employee);

			model.addAttribute("workHours", workHours);
			model.addAttribute("salary", salary);
			model.addAttribute("taskRewards", taskRewards);
			model.addAttribute("nationalID", employee.getNationalID());
			model.addAttribute("payPerHour", employee.getPayPerHour());
			injectLoggedInEmployeeInfo(model, employeeDetails, employee);

			return "salary";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}
	
	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails, Employee employee) {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		int unread = 0;
		for(int i = 0; i < employee.getNotifications().size(); i++) {
			if(!employee.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		model.addAttribute("notifications", employee.getNotifications());
		model.addAttribute("unread", unread);
	}

	public static boolean getActive() {
		return active;
	}

	public static void setActive(boolean newActive) {
		active = newActive;
	}
}
