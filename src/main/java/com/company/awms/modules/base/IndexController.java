package com.company.awms.modules.base;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.security.EmployeeDetails;

@Controller
public class IndexController {

    private EmployeeService employeeService;

    @Autowired
    public IndexController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping({ "/", "/index" })
    public String index(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
        try {
            Employee employee = employeeService.getEmployee(employeeDetails.getID());
            model.addAttribute("employee", employee);
            model.addAttribute("credentials", Notification.getCredentials());
            return "base/employees/index";
        } catch (IOException e) {
            e.printStackTrace();
            return "errors/notFound";
        } catch (Exception e) {
            return "errors/internalServerError";
        }
    }

    @GetMapping({ "/login" })
    public String login() {
        try {
            return "login";
        } catch (Exception e) {
        	e.printStackTrace();
            return "errors/internalServerError";
        }
    }

}
