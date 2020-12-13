package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static boolean active = true;

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/password")
    public String updatePassword(@RequestParam String newPassword, @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){

        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("mismatch", true);

            return "newPassword";
        }

        try {
            Employee employee = this.employeeService.updatePassword(newPassword, employeeDetails.getID());

            model.addAttribute("employee", employee);
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "index";
        } catch (IOException e){
            return "notFound";
        } catch (Exception e){
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @GetMapping("/password/new")
    public String getPasswordUpdate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException{
        injectLoggedInEmployeeInfo(model, employeeDetails);
        model.addAttribute("mismatch", false);

        return "newPassword";
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

    @GetMapping("/dismiss")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, String noteNum) {
		try{
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			injectLoggedInEmployeeInfo(model, employeeDetails);
			Employee employee = this.employeeService.getEmployee(employeeDetails.getID());
            model.addAttribute("employee", employee);
            return "redirect:/";
		}catch(Exception e) {
			return "internalServerError";
		}
	}
    
    public static boolean getActive() {
        return active;
    }
}
