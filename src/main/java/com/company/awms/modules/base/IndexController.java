package com.company.awms.modules.base;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
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

        try {
            Employee employee = this.employeeService.getEmployee(employeeDetails.getID());

            model.addAttribute("employee", employee);
            injectLoggedInEmployeeInfo(model, employeeDetails);
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
}
