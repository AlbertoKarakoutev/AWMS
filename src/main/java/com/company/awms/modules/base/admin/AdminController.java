package com.company.awms.modules.base.admin;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.modules.base.documents.DocumentService;
import com.company.awms.modules.base.documents.data.DocInfoDTO;
import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.forum.ForumService;
import com.company.awms.modules.base.schedule.ScheduleService;
import com.company.awms.security.EmployeeDetails;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private EmployeeService employeeService;
	private DocumentService documentService;
	private ScheduleService scheduleService;
	private ForumService forumService;
	private AdminService adminService;

	@Autowired
	public AdminController(EmployeeService employeeService, DocumentService documentService, ScheduleService scheduleService, ForumService forumService, AdminService adminService) {
		this.documentService = documentService;
		this.employeeService = employeeService;
		this.scheduleService = scheduleService;
		this.forumService = forumService;
		this.adminService = adminService;
	}

	// Employee methods
	@GetMapping("/employee/all")
	public String getEmployees(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<Employee> employees = employeeService.getAllEmployeesDTOs();
			model.addAttribute("employees", employees);
			model.addAttribute("departments", adminService.getDepartmentDTOs());

			return "base/employees/employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/employee/register", consumes = "text/plain")
	public String registerEmployee(@RequestBody String data, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<Employee> employees = new ArrayList<>();
			employees.add(employeeService.registerEmployee(data));
			model.addAttribute("employees", employees);
			return "base/employees/employees";
		} catch (Exception e) {

			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/edit/{employeeID}")
	public String editEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("employee", employee);
			model.addAttribute("newEmployee", false);
			model.addAttribute("departments", adminService.getDepartmentDTOs());
			return "base/employees/editEmployee";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/register")
	public String registerEmployee(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			model.addAttribute("departments", adminService.getDepartmentDTOs());
			model.addAttribute("newEmployee", true);
			return "base/employees/editEmployee";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/delete/")
	public String deleteEmployee(@AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String employeeID, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			employeeService.deleteEmployee(employeeID);
			return "redirect:/admin/employee/all";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping(value = "/personal/{employeeID}")
	public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, @PathVariable String employeeID, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<DocInfoDTO> documents = documentService.getPersonalDocumentsInfo(employeeID);

			model.addAttribute("documents", documents);
			model.addAttribute("type", "admin-edit");
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());

			return "base/documents/documents";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/employee/update", consumes = "text/plain")
	public String updateEmployeeInfo(@RequestBody String data, @RequestParam String employeeId, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<Employee> employee = new ArrayList<>();
			employee.add(employeeService.updateEmployeeInfo(employeeId, data));
			model.addAttribute("departments", adminService.getDepartmentDTOs());
			model.addAttribute("employees", employee);
			return "base/employees/employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/search")
	public String searchEmployees(@RequestParam String searchTerm, @RequestParam String type, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<Employee> employees = employeeService.getAllEmployees();
			List<Employee> foundEmployees = employeeService.searchEmployees(employees, searchTerm, type);
			model.addAttribute("employees", foundEmployees);
			model.addAttribute("departments", adminService.getDepartmentDTOs());
			return "base/employees/employees";

		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/leaves/{employeeID}")
	public String getLeaves(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String employeeID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("employeeID", employeeID);
			model.addAttribute("leaves", employee.getLeaves());
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			return "base/employees/leaves";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@PostMapping("/employee/approveLeave")
	public String approveLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String employeeID, @RequestParam String paid, @RequestParam String startDate,
			@RequestParam String endDate) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			employeeService.approveLeave(employeeID, Boolean.parseBoolean(paid), startDate, endDate);
			return "redirect:/";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping("/employee/denyLeave")
	public String denyLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String employeeID, @RequestParam String startDate, @RequestParam String endDate) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			employeeService.denyLeave(employeeID, startDate, endDate);
			return "redirect:/";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/deleteLeave")
	public String deleteLeave(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam String employeeID, @RequestParam String leave) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			employeeService.deleteLeave(employeeID, leave);
			return "redirect:/admin/employee/leaves/" + employeeID;
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping("/email")
	public String setNotificationEmailCredentials(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody String data) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Object[] credentialObject = adminService.setNotificationEmailCredentials(data);
			Notification.setCredentials((String)credentialObject[0], (String)credentialObject[1], (boolean)credentialObject[2]);
			return "redirect:/";
		}catch(Exception e) {
			return "errors/internalServerError";
		}

	}

	// Forum methods
	@GetMapping("/forum/delete/{threadID}")
	public String deleteThread(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String threadID) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			forumService.deleteThread(threadID);
			return "redirect:/forum";
		} catch (Exception e) {
			return "internalServerError";
		}
	}

	// Schedule methods
	@GetMapping(value = "/schedule/add")
	public ResponseEntity<String> addWorkDay(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String employeeNationalID, @RequestParam String date, @RequestParam String startShift,
			@RequestParam String endShift) {
		try {
			scheduleService.addWorkDay(employeeNationalID, date, true, startShift, endShift);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/schedule/delete")
	public ResponseEntity<String> deleteWorkDay(Model model, @RequestParam String employeeNationalID, @RequestParam String date, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			scheduleService.deleteWorkDay(employeeNationalID, date);

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/schedule/apply")
	public String applySchedule(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			scheduleService.applySchedule();
			return "redirect:/schedule/?month=" + YearMonth.now();
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	// Document methods
	@PostMapping(value = "/personal/document/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadPersonalDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			documentService.uploadPersonalDocument(file, employeeID, true);

			List<DocInfoDTO> documents = documentService.getPersonalDocumentsInfo(employeeID);

			model.addAttribute("documents", documents);
			model.addAttribute("type", "admin-edit");
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());

			return "base/documents/documents";
		} catch (IOException e) {
			return "errors/badRequest";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/document/personal/delete/{documentID}")
	public String deletePersonalDocument(@PathVariable int documentID, @RequestParam String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			documentService.deletePersonalDocument(documentID, ownerID);

			List<DocInfoDTO> documents = documentService.getPersonalDocumentsInfo(ownerID);

			model.addAttribute("type", "admin-edit");
			model.addAttribute("documents", documents);
			Employee employee = employeeService.getEmployee(ownerID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());

			return "base/documents/documents";
		} catch (IllegalArgumentException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	// Admin methods
	@PostMapping(value = "/modules/set", consumes = "application/json")
	public ResponseEntity<String> setModules(@RequestBody String newStates, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException {
		try {
			adminService.setModules(newStates);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch(IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch(Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/departments/view")
	public ResponseEntity<String> getDepartment(@RequestParam String departmentCode, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			String departmentString = adminService.getDepartmentAsString(departmentCode);
			return new ResponseEntity<>(departmentString, HttpStatus.OK);
		}catch(IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/departments/set", consumes = "application/json")
	public ResponseEntity<String> setDepartment(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody Object departmentObj) throws ParseException {
		try {
			scheduleService.setDepartment(departmentObj);
			Map<String, String> departmentDTOs = adminService.getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/modules")
	public String getModules(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Map<String, Boolean> controllerConditions = adminService.getModules();
			model.addAttribute("modules", controllerConditions);
			return "base/admin/modules";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}
	
	@GetMapping("/departments")
	public String getDepartments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			Map<String, String> departmentDTOs = adminService.getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
		return "base/admin/departments";
	}
	
	@PostMapping(value = "/departments/delete", consumes = "application/json")
	public String deleteDepartment(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody Object departmentObj) throws ParseException {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			adminService.deleteDepartment(departmentObj);
			Map<String, String> departmentDTOs = adminService.getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			return "redirect:/admin/departments";
		} catch(IOException e) {
			return "errors/notFound";
		}catch (Exception e) {
			return "errors/internalServerError";
		}
	}

}
