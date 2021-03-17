package com.company.awms.modules.base.admin;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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

import com.company.awms.modules.base.admin.data.Department;
import com.company.awms.modules.base.admin.data.Module;
import com.company.awms.modules.base.documents.DocumentService;
import com.company.awms.modules.base.documents.data.DocInfoDTO;
import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.Notification;
import com.company.awms.modules.base.forum.ForumService;
import com.company.awms.modules.base.schedule.ScheduleService;
import com.company.awms.security.EmployeeDetails;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private EmployeeService employeeService;
	private DocumentService documentService;
	private ScheduleService scheduleService;
	private ForumService forumService;

	@Autowired
	public AdminController(EmployeeService employeeService, DocumentService documentService, ScheduleService scheduleService,ForumService forumService) {
		this.documentService = documentService;
		this.employeeService = employeeService;
		this.scheduleService = scheduleService;
		this.forumService = forumService;
	}

	// Employee methods
	@GetMapping("/employee/all")
	public String getEmployees(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employees = this.employeeService.getAllEmployeesDTOs();
			model.addAttribute("employees", employees);
			model.addAttribute("departments", getDepartmentDTOs());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/employees/employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/employee/register", consumes = "text/plain")
	public String registerEmployee(@RequestBody String data, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employee = new ArrayList<>();
			employee.add(employeeService.registerEmployee(data));
			model.addAttribute("employees", employee);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "base/employees/employees";
		} catch (Exception e) {

			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/edit/{employeeID}")
	public String editEmployee(@PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("employee", employee);
			model.addAttribute("newEmployee", false);
			model.addAttribute("departments", getDepartmentDTOs());
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "base/employees/editEmployee";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/register")
	public String registerEmployee(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			model.addAttribute("departments", getDepartmentDTOs());
			model.addAttribute("newEmployee", true);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "base/employees/editEmployee";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/delete/")
	public String deleteEmployee(@AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String employeeID, Model model){
		if(!employeeDetails.getRole().equals("ADMIN")) {
			return "errors/notAuthorized";
		}
		try {
			employeeService.deleteEmployee(employeeID);
			return "redirect:/admin/employee/all";
		}catch(Exception e) {
			return "errors/internalServerError";
		}
	}
	
	@GetMapping(value = "/personal/{employeeID}")
	public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, @PathVariable String employeeID, Model model) {
		try {
			List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeID);

			model.addAttribute("documents", documents);
			model.addAttribute("type", "admin-edit");
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/documents/documents";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/employee/update", consumes = "text/plain")
	public String updateEmployeeInfo(@RequestBody String data, @RequestParam String employeeId, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employee = new ArrayList<>();
			employee.add(employeeService.updateEmployeeInfo(employeeId, data));
			model.addAttribute("departments", getDepartmentDTOs());
			model.addAttribute("employees", employee);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "base/employees/employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/search")
	public String searchEmployees(@RequestParam String searchTerm, @RequestParam String type, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employees = this.employeeService.getAllEmployees();
			List<Employee> foundEmployees = this.employeeService.searchEmployees(employees, searchTerm, type);
			model.addAttribute("employees", foundEmployees);
			model.addAttribute("departments", getDepartmentDTOs());
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "base/employees/employees";

		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/leaves/{employeeID}")
	public String getLeaves(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String employeeID) {
		try {
			injectLoggedInEmployeeInfo(model, employeeDetails);
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
		try {
			injectLoggedInEmployeeInfo(model, employeeDetails);
			employeeService.approveLeave(employeeID, Boolean.parseBoolean(paid), startDate, endDate);
			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping("/employee/denyLeave")
	public String denyLeave(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String noteNum, @RequestParam String employeeID, @RequestParam String startDate, @RequestParam String endDate) {
		try {
			injectLoggedInEmployeeInfo(model, employeeDetails);
			employeeService.denyLeave(employeeID, startDate, endDate);
			return "redirect:/";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/employee/deleteLeave")
	public String deleteLeave(@RequestParam String employeeID, @RequestParam String leave) {
		try {
			employeeService.deleteLeave(employeeID, leave);
			return "redirect:/admin/employee/leaves/" + employeeID;
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}
	
	@PostMapping("/email")
	public String setNotificationEmailCredentials(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody String data) {
		String[] dataValues = data.split("\\n");
		Map<String, String> credentials = new HashMap<>();
		for (String field : dataValues) {
			field = field.substring(0, field.length() - 1);
			credentials.put(field.split("=")[0], field.split("=")[1]);
		}
		String status = "off";
		if(credentials.get("emailNotifications") != null) {
			status = credentials.get("emailNotifications");
		}
		Notification.setCredentials(credentials.get("username"), credentials.get("password"), (status.equals("on") ? true : false));
		return "redirect:/";
		
	}
	
	//Forum methods
	@GetMapping("/forum/delete/{threadID}")
	public String deleteThread(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @PathVariable String threadID) {
		try {
			forumService.deleteThread(threadID);
			return "redirect:/forum";
		}catch(Exception e) {
			return "internalServerError";
		}
	}
	
	// Schedule methods
	@GetMapping(value = "/schedule/add")
	public ResponseEntity<String> addWorkDay(Model model, @AuthenticationPrincipal EmployeeDetails employeeDetails, @RequestParam String employeeNationalID, @RequestParam String date, @RequestParam String startShift, @RequestParam String endShift) {
		try {
			scheduleService.addWorkDay(employeeNationalID, date, true, startShift, endShift);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return  new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/schedule/delete")
	public ResponseEntity<String> deleteWorkDay(Model model, @RequestParam String employeeNationalID, @RequestParam String date, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			this.scheduleService.deleteWorkDay(employeeNationalID, date);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			
			return  new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/schedule/apply")
	public String applySchedule() {
		try {
			this.scheduleService.applySchedule();

			return "redirect:/schedule/?month="+YearMonth.now();
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	// Document methods
	@PostMapping(value = "/personal/document/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadPersonalDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			this.documentService.uploadPersonalDocument(file, employeeID, true);

			List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeID);

			model.addAttribute("documents", documents);
			model.addAttribute("type", "admin-edit");
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/documents/documents";
		} catch (IOException e) {
			return "erorrs/badRequest";
		} catch (Exception e) {
			System.out.println(e);
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/document/personal/delete/{documentID}")
	public String deletePersonalDocument(@PathVariable int documentID, @RequestParam String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			this.documentService.deletePersonalDocument(documentID, ownerID);

			List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(ownerID);

			model.addAttribute("type", "admin-edit");
			model.addAttribute("documents", documents);
			Employee employee = employeeService.getEmployee(ownerID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/documents/documents";
		} catch (IllegalArgumentException e) {
			return "erorrs/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	// Active modules methods
	public Map<String, Boolean> getAllModulesStatesLogic() {
		Map<String, Boolean> conditions = new HashMap<String, Boolean>();
		List<Module> modules = employeeService.getAllModules();
		for(Module module : modules) {
			StringBuilder moduleName = new StringBuilder(module.getName());
			moduleName.setCharAt(0, Character.toUpperCase(moduleName.charAt(0)));
			String name = moduleName.toString();
			conditions.put(name, module.isActive());
		}
		return conditions;
	}

	
	@GetMapping("/modules")
	public String getModulesStates(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			Map<String, Boolean> controllerConditions = getAllModulesStatesLogic();
			model.addAttribute("modules", controllerConditions);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "base/admin/modules";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/modules/set", consumes = "application/json")
	public ResponseEntity<String> setModulesStates(@RequestBody String updatedActives, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws IOException {
		String updatedActivitiesFormatted = updatedActives.substring(19, updatedActives.length() - 2).replaceAll("\\\\", "");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Boolean> actives = mapper.readValue(updatedActivitiesFormatted, HashMap.class);
		for (String key : actives.keySet()) {
			Module module = employeeService.getModule(key.toLowerCase());
			module.setActive(actives.get(key));
			employeeService.updateModule(module);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// Department methods
	private Map<String, String> getDepartmentDTOs() throws Exception {
		Map<String, String> departmentDTOs = new HashMap<>();
		for (Department department : scheduleService.getAllDepartments()) {
			departmentDTOs.put(Character.toString(department.getDepartmentCode()), department.getName());
		}
		return departmentDTOs;
	}

	@GetMapping("/departments")
	public String getDepartments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			Map<String, String> departmentDTOs = getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			injectLoggedInEmployeeInfo(model, employeeDetails);
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
		return "base/admin/departments";
	}

	@GetMapping("/departments/view")
	public ResponseEntity<String> getDepartment(@RequestParam String departmentCode, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			Department department = scheduleService.getDepartment(departmentCode);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.ALWAYS);
			String departmentString = mapper.writeValueAsString(department);
			return new ResponseEntity<>(departmentString, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/departments/set", consumes = "application/json")
	public  ResponseEntity<String> setDepartment(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody Object departmentObj) throws ParseException {
		try {
			scheduleService.setDepartment(departmentObj);
			Map<String, String> departmentDTOs = getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/departments/delete", consumes = "application/json")
	public String deleteDepartment(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody Object departmentObj) throws ParseException {

		JSONObject departmentBody = new JSONObject((Map) departmentObj);
		String key = (String)departmentBody.get("departmentCode");
		try {
			Map<String, String> departmentDTOs = getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			scheduleService.deleteDepartment(key);
			return "redirect:/admin/departments";
		} catch (Exception e) {
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
