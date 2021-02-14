package com.company.awms.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
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

import com.company.awms.data.documents.DocInfoDTO;
import com.company.awms.data.employees.Employee;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.DocumentService;
import com.company.awms.services.EmployeeService;
import com.company.awms.services.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private EmployeeService employeeService;
	private DocumentService documentService;
	private ScheduleService scheduleService;

	@Autowired
	public AdminController(EmployeeService employeeService, DocumentService documentService, ScheduleService scheduleService) {
		this.documentService = documentService;
		this.employeeService = employeeService;
		this.scheduleService = scheduleService;
	}

	// Employee methods
	@GetMapping("/employee/all")
	public String getEmployees(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employees = this.employeeService.getAllEmployees();
			model.addAttribute("employees", employees);
			model.addAttribute("departments", getDepartmentDTOs());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	@PostMapping(value = "/employee/register", consumes = "text/plain")
	public String registerEmployee(@RequestBody String data, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			List<Employee> employee = new ArrayList<>();
			employee.add(employeeService.registerEmployee(data));
			model.addAttribute("employees", employee);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "employees";
		} catch (Exception e) {
			return "internalServerError";
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
			return "editEmployee";
		} catch (Exception e) {
			return "internalServerError";
		}
	}

	@GetMapping("/employee/register")
	public String registerEmployee(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			model.addAttribute("departments", getDepartmentDTOs());
			model.addAttribute("newEmployee", true);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			return "editEmployee";
		} catch (Exception e) {
			return "internalServerError";
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

			return "documents";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
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
			return "employees";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
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
			return "employees";

		} catch (Exception e) {
			return "internalServerError";
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
			return "leaves";
		} catch (Exception e) {
			return "internalServerError";
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
			return "internalServerError";
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
			return "internalServerError";
		}
	}

	@GetMapping("/employee/deleteLeave")
	public String deleteLeave(@RequestParam String employeeID, @RequestParam String leave) {
		try {
			employeeService.deleteLeave(employeeID, leave);
			return "redirect:/admin/employee/leaves/" + employeeID;
		} catch (Exception e) {
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
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/schedule/delete")
	public ResponseEntity<String> deleteWorkDay(Model model, @RequestParam String employeeNationalID, @RequestParam String date, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			this.scheduleService.deleteWorkDay(employeeNationalID, date);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			model.addAttribute(YearMonth.now());
			
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
			return "internalServerError";
		}
	}

	// Document methods
	@PostMapping(value = "/personal/document/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadPersonalDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			this.documentService.uploadPersonalDocument(file, employeeID);

			List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeID);

			model.addAttribute("documents", documents);
			model.addAttribute("type", "admin-edit");
			Employee employee = employeeService.getEmployee(employeeID);
			model.addAttribute("name", employee.getFirstName() + " " + employee.getLastName());
			model.addAttribute("ownerID", employee.getID());
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "documents";
		} catch (IOException e) {
			return "badRequest";
		} catch (Exception e) {
			System.out.println(e);
			return "internalServerError";
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

			return "documents";
		} catch (IllegalArgumentException e) {
			return "notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "internalServerError";
		}
	}

	// Active modules methods
	public static Map<String, Boolean> getActivesMethod() {
		File controllerDir = new File("target/classes/com/company/awms/controllers");
		File[] controllers = controllerDir.listFiles();
		Map<String, Boolean> controllerConditions = new HashMap<>();
		for (File controllerFile : controllers) {
			String controllerName = controllerFile.getName().split("\\.")[0];
			try {
				if (!controllerName.equals("updatedActivesController") && !controllerName.equals("AdminController") && !controllerName.equals("IndexController")) {
					Class<?> controller = Class.forName("com.company.awms.controllers." + controllerName);
					Method active = controller.getMethod("getActive");
					controllerConditions.put(controllerName.split("Controller")[0], (boolean) active.invoke(null, null));
				}
			} catch (Exception e) {
			}
		}
		return controllerConditions;
	}

	@GetMapping("/modules")
	public String getActives(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		try {
			Map<String, Boolean> controllerConditions = getActivesMethod();
			model.addAttribute("modules", controllerConditions);
			injectLoggedInEmployeeInfo(model, employeeDetails);

			return "modules";
		} catch (Exception e) {
			return "internalServerError";
		}

	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/modules/set", consumes = "application/json")
	public String setActives(@RequestBody String updatedActives, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) throws JsonMappingException, JsonProcessingException {
		String updatedActivitiesFormatted = updatedActives.substring(19, updatedActives.length() - 2).replaceAll("\\\\", "");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Boolean> actives = mapper.readValue(updatedActivitiesFormatted, HashMap.class);
		for (String key : actives.keySet()) {
			try {
				Class<?> controller = Class.forName("com.company.awms.controllers." + key + "Controller");
				Method active = controller.getMethod("setActive", boolean.class);
				active.invoke(null, actives.get(key));
			} catch (Exception e) {
				return "internalServerError";
			}
		}
		return "redirect:/";
	}

	// Department methods
	private Map<String, String> getDepartmentDTOs() throws Exception {
		Map<String, String> departmentDTOs = new HashMap<>();
		for (int i = 97; i < 123; i++) {
			String departmentCode = Character.toString((char) i);
			JSONObject department = scheduleService.getDepartment(departmentCode);
			if (department == null) {
				continue;
			}
			departmentDTOs.put(departmentCode, (String) department.get("Name"));
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
			return "internalServerError";
		}
		return "departments";
	}

	@GetMapping("/departments/view")
	public ResponseEntity<JSONObject> getDepartment(@RequestParam String departmentCode, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			JSONObject department = scheduleService.getDepartment(departmentCode);
			return new ResponseEntity<>(department, HttpStatus.OK);
		} catch (Exception e) {
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
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/departments/delete", consumes = "application/json")
	public String deleteDepartments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestBody Object departmentObj) throws ParseException {

		JSONObject departmentBody = new JSONObject((Map) departmentObj);
		String key = null;
		Set<String> keys = departmentBody.keySet();
		Iterator<String> keyIterator = keys.iterator();
		while (keyIterator.hasNext()) {
			key = keyIterator.next();
			break;
		}
		try {
			Map<String, String> departmentDTOs = getDepartmentDTOs();
			model.addAttribute("departments", departmentDTOs);
			injectLoggedInEmployeeInfo(model, employeeDetails);
			scheduleService.deleteDepartment(key);
			return "redirect:/admin/departments";
		} catch (Exception e) {
			return "internalServerError";
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
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}
}
