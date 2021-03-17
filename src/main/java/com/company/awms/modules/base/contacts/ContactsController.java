package com.company.awms.modules.base.contacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.security.EmployeeDetails;

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
	    employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
        try {
            Employee owner = employeeService.getOwner();
            List<Employee> managers = employeeService.getManagers();
            model.addAttribute("owner", owner);
            model.addAttribute("managers", managers);
            
            return "base/contacts/contacts";
        } catch (IOException e) {
            return "errors/notFound";
        } catch (Exception e){
            e.printStackTrace();
            return "errors/internalServerError";
        }
    }
}
