package com.company.awms.modules.base.employees;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.security.EmployeeDetails;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	private EmployeeService employeeService;

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping("/manager/department/")
	public String getDepartmentEmployees(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String employeeID) {
		try {
			List<Employee> employees = employeeService.getDepartmentEmployeesDTOs(employeeID);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			model.addAttribute("employees", employees);
			return "base/employees/employees";
		} catch (IllegalAccessException e) {
			return "errors/notAuthorized";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@PostMapping("/password")
	public String updatePassword(@RequestParam String newPassword, @RequestParam String confirmPassword, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			if (!newPassword.equals(confirmPassword)) {
				injectLoggedInEmployeeInfo(model, employeeDetails);
				model.addAttribute("mismatch", true);

				return "base/employees/newPassword";
			}

			Employee employee = this.employeeService.updatePassword(newPassword, employeeDetails.getID());

			model.addAttribute("employee", employee);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/employees/index";
		} catch (IOException e) {
			return "erorrs/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "erorrs/internalServerError";
		}
	}

	@GetMapping("/password/new")
	public String getPasswordUpdate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException {

		injectLoggedInEmployeeInfo(model, employeeDetails);
		model.addAttribute("mismatch", false);

		return "base/employees/newPassword";
	}

	@GetMapping("/manager/leaves/{employeeID}")
	public String getLeavesAsManager(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String employeeID) {
		try {
			if (!employeeDetails.getRole().equals("MANAGER")) {
				return "errors/notAuthorized";
			}
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("employeeID", employeeID);
			model.addAttribute("leaves", employee.getLeaves());
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			return "base/employees/leaves";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/leaves")
	public String getLeaves(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = employeeService.getEmployee(employeeDetails.getID());
			model.addAttribute("leaves", employee.getLeaves());
			return "base/employees/leaves";
		} catch (Exception e) {
			return "erorrs/internalServerError";
		}
	}

	@GetMapping("/requestLeave")
	public String requestLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String paidStr, @RequestParam String startDate, @RequestParam String endDate) {

		try {
			boolean paid = Boolean.parseBoolean(paidStr);
			employeeService.requestLeave(employeeDetails.getID(), paid, startDate, endDate);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			return "erorrs/internalServerError";
		}
	}

	private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		Employee user = employeeService.getEmployee(employeeDetails.getID());
		int unread = 0;
		for (int i = 0; i < user.getNotifications().size(); i++) {
			if (!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		model.addAttribute("extModules", employeeService.getExtensionModulesDTOs());
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

	@GetMapping("/dismiss")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, String noteNum) {

		try {
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "redirect:/";
		} catch (Exception e) {
			return "erorrs/internalServerError";
		}
	}

}
