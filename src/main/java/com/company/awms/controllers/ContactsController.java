package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class ContactsController {
    private EmployeeService employeeService;

    private static final boolean active = true;
    
    @Autowired
    public ContactsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("contacts")
    public String getContacts(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        try {
            Employee owner = this.employeeService.getOwner();
            List<Employee> managers = this.employeeService.getManagers();
            injectLoggedInEmployeeInfo(model, employeeDetails);
            model.addAttribute("owner", owner);
            model.addAttribute("managers", managers);
            injectLoggedInEmployeeInfo(model, employeeDetails);
            
            return "contacts";
        } catch (IOException e) {
            e.printStackTrace();
            return "notFound";
        } catch (Exception e){
            e.printStackTrace();
            return "internalServerError";
        }
    }

    private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException{
        model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
        model.addAttribute("employeeEmail", employeeDetails.getUsername());
        model.addAttribute("employeeID", employeeDetails.getID());
        Employee user = employeeService.getEmployee(employeeDetails.getID());
        model.addAttribute("notifications", user.getNotifications());
    }
    
    public static boolean getActive() {
		return active;
	}
}
