package com.company.awms.modules.base.employees;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import com.company.awms.modules.base.employees.data.Notification;
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
	public String getDepartmentEmployees(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String managerID, @RequestParam Optional<Integer> page) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<Employee> employees = employeeService.getDepartmentEmployeesDTOsByPage(managerID, 1);
			
			if(page.isPresent()) {
				employees = employeeService.getDepartmentEmployeesDTOsByPage(managerID, page.get());
				model.addAttribute("page", page.get());
			}else {
				model.addAttribute("page", 1);
			}

			model.addAttribute("type", "all");
			model.addAttribute("link", "/employee/manager/department/?managerID="+managerID+"&&");
			model.addAttribute("pageCount", (int)Math.ceil((double)employeeService.getDepartmentEmployeesDTOs(managerID).size()/10));
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
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			if (!newPassword.equals(confirmPassword)) {
				model.addAttribute("mismatch", true);
				return "base/employees/newPassword";
			}

			Employee employee = this.employeeService.updatePassword(newPassword, employeeDetails.getID());

			model.addAttribute("employee", employee);

			return "base/employees/index";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/password/new")
	public String getPasswordUpdate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException {

		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		model.addAttribute("mismatch", false);

		return "base/employees/newPassword";
	}

	@GetMapping("/manager/leaves/{leaveEmployeeID}")
	public String getLeavesAsManager(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String leaveEmployeeID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			if (!employeeDetails.getRole().equals("MANAGER")) {
				return "errors/notAuthorized";
			}
			Employee employee = employeeService.getEmployee(leaveEmployeeID);
			model.addAttribute("leaveEmployeeID", leaveEmployeeID);
			model.addAttribute("leaves", employee.getLeaves());
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			return "base/employees/leaves";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/leaves")
	public String getLeaves(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Employee employee = employeeService.getEmployee(employeeDetails.getID());
			model.addAttribute("leaves", employee.getLeaves());
			return "base/employees/leaves";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/requestLeave")
	public String requestLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam boolean paid, @RequestParam String startDate, @RequestParam String endDate) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			employeeService.requestLeave(employeeDetails.getID(), paid, startDate, endDate);
			return "redirect:/employee/leaves";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/dismiss")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Notification.setAsRead(employeeService, employeeDetails.getID(), Integer.parseInt(noteNum));
			return "redirect:/";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

}
