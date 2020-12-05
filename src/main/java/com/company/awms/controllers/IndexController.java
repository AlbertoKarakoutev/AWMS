package com.company.awms.controllers;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

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
            return "index";
        } catch (IOException e) {
            e.printStackTrace();
            return "notFound";
        }
    }
}
