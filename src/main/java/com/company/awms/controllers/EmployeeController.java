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
    public String getPasswordUpdate(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        injectLoggedInEmployeeInfo(model, employeeDetails);
        model.addAttribute("mismatch", false);

        return "newPassword";
    }

    private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails){
        model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
        model.addAttribute("employeeEmail", employeeDetails.getUsername());
        model.addAttribute("employeeID", employeeDetails.getID());
    }

    public static boolean getActive() {
        return active;
    }
}
