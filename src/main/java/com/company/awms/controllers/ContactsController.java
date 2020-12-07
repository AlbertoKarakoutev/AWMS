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

    @Autowired
    public ContactsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("contacts")
    public String getContacts(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        try {
            Employee owner = this.employeeService.getOwner();
            List<Employee> managers = this.employeeService.getManagers();

            model.addAttribute("owner", owner);
            model.addAttribute("managers", managers);

            return "contacts";
        } catch (IOException e) {
            e.printStackTrace();
            return "notFound";
        } catch (Exception e){
            e.printStackTrace();
            return "internalServerError";
        }
    }
}
