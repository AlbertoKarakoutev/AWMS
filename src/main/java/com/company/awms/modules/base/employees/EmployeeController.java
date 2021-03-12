package com.company.awms.modules.base.employees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.security.EmployeeDetails;

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
        if (!active) {
            return "erorrs/notFound";
        }

        try {
            if(!newPassword.equals(confirmPassword)){
                injectLoggedInEmployeeInfo(model, employeeDetails);
                model.addAttribute("mismatch", true);

                return "base/employees/newPassword";
            }

            Employee employee = this.employeeService.updatePassword(newPassword, employeeDetails.getID());

            model.addAttribute("employee", employee);
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/employees/index";
        } catch (IOException e){
            return "erorrs/notFound";
        } catch (Exception e){
            e.printStackTrace();
            return "erorrs/internalServerError";
        }
    }

    @GetMapping("/password/new")
    public String getPasswordUpdate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException{
        if (!active) {
            return "erorrs/notFound";
        }

        injectLoggedInEmployeeInfo(model, employeeDetails);
        model.addAttribute("mismatch", false);

        return "base/employees/newPassword";
    }
    
    @GetMapping("/leaves")
    public String getLeaves(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        if (!active) {
            return "erorrs/notFound";
        }
        try {
	        injectLoggedInEmployeeInfo(model, employeeDetails);
	        Employee employee = employeeService.getEmployee(employeeDetails.getID());
	        model.addAttribute("leaves", employee.getLeaves());
	        return "base/employees/leaves";
        }catch(Exception e) {
        	return "erorrs/internalServerError";
        }
    }
    
    @GetMapping("/requestLeave")
    public String requestLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails,  @RequestParam String paidStr, @RequestParam String startDate, @RequestParam String endDate) {
    	if (!active) {
            return "erorrs/notFound";
        }
    	
    	try {
    		boolean paid = Boolean.parseBoolean(paidStr);
    		employeeService.requestLeave(employeeDetails.getID(), paid, startDate, endDate);
    		injectLoggedInEmployeeInfo(model, employeeDetails);
    		return"redirect:/";
    	}catch(Exception e) {
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
		for(int i = 0; i < user.getNotifications().size(); i++) {
			if(!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		System.out.println(employeeService.getExtensionModulesDTOs().size());
		model.addAttribute("extModules", employeeService.getExtensionModulesDTOs());
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

    @GetMapping("/dismiss")
	public String dismiss(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, String noteNum) {
        if (!active) {
            return "erorrs/notFound";
        }

        try{
			employeeService.setNotificationRead(employeeDetails.getID(), Integer.parseInt(noteNum));
			injectLoggedInEmployeeInfo(model, employeeDetails);
            return "redirect:/";
		}catch(Exception e) {
			return "erorrs/internalServerError";
		}
	}
    
    public static boolean getActive() {
        return active;
    }
}
