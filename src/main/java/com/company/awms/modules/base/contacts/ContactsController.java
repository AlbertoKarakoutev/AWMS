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

    private static final boolean active = true;
    
    @Autowired
    public ContactsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("contacts")
    public String getContacts(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        if (!active) {
            return "errors/notFound";
        }

        try {
            Employee owner = this.employeeService.getOwner();
            List<Employee> managers = this.employeeService.getManagers();
            injectLoggedInEmployeeInfo(model, employeeDetails);
            model.addAttribute("owner", owner);
            model.addAttribute("managers", managers);
            injectLoggedInEmployeeInfo(model, employeeDetails);
            
            return "base/contacts/contacts";
        } catch (IOException e) {
            e.printStackTrace();
            return "erorrs/notFound";
        } catch (Exception e){
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
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}
    
    public static boolean getActive() {
		return active;
	}
}
