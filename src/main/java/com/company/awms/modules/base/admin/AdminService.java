package com.company.awms.modules.base.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.modules.base.admin.data.Department;
import com.company.awms.modules.base.admin.data.DepartmentRepo;
import com.company.awms.modules.base.admin.data.Module;
import com.company.awms.modules.base.admin.data.ModuleRepo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdminService {

	private DepartmentRepo departmentRepo;
	private ModuleRepo moduleRepo;

	@Autowired
	public AdminService(DepartmentRepo departmentRepo, ModuleRepo moduleRepo) {

		this.departmentRepo = departmentRepo;
		this.moduleRepo = moduleRepo;
		
	}
	
	public Map<String, Boolean> getModules() {
		Map<String, Boolean> conditions = new HashMap<String, Boolean>();
		List<Module> modules = moduleRepo.findAll();
		for(Module module : modules) {
			StringBuilder moduleName = new StringBuilder(module.getName());
			moduleName.setCharAt(0, Character.toUpperCase(moduleName.charAt(0)));
			String name = moduleName.toString();
			conditions.put(name, module.isActive());
		}
		return conditions;
	}
		
	public Map<String, String> getDepartmentDTOs() throws Exception {
		Map<String, String> departmentDTOs = new HashMap<>();
		for (Department department : departmentRepo.findAll()) {
			departmentDTOs.put(Character.toString(department.getDepartmentCode()), department.getName());
		}
		return departmentDTOs;
	}

	public String getDepartmentAsString(String departmentCode) throws IOException {
		Optional<Department> departmentOptional = departmentRepo.findByDepartmentCode(departmentCode);
		if (departmentOptional.isEmpty()) {
			throw new IOException();
		}
		Department department = departmentOptional.get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.ALWAYS);
		String departmentString = mapper.writeValueAsString(department);
		return departmentString;
	}
	
	public void setModules(String newStates) throws IOException {
		String updatedActivitiesFormatted = newStates.substring(19, newStates.length() - 2).replaceAll("\\\\", "");
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Boolean> actives = mapper.readValue(updatedActivitiesFormatted, HashMap.class);
		for (String key : actives.keySet()) {
			Optional<Module> moduleOptional = moduleRepo.findByName(key.toLowerCase());
			if (moduleOptional.isEmpty()) {
				throw new IOException();
			}
			Module module = moduleOptional.get();
			module.setActive(actives.get(key));
			moduleRepo.save(module);
		}
	}
	
	public void deleteDepartment(Object departmentObj) throws ParseException, IOException {

		JSONObject departmentBody = new JSONObject((Map<?, ?>) departmentObj);
		String key = (String) departmentBody.get("departmentCode");

		Optional<Department> departmentOptional = departmentRepo.findByDepartmentCode(key);
		if (departmentOptional.isEmpty()) {
			throw new IOException();
		}
		
		Department toBeDeleted = departmentOptional.get();
		departmentRepo.deleteById(toBeDeleted.getID());
		
	}

	public Object[] setNotificationEmailCredentials(String data) {
		String[] dataValues = data.split("\\n");
		Map<String, String> credentials = new HashMap<>();
		for (String field : dataValues) {
			field = field.substring(0, field.length() - 1);
			credentials.put(field.split("=")[0], field.split("=")[1]);
		}
		String status = "off";
		if (credentials.get("emailNotifications") != null) {
			status = credentials.get("emailNotifications");
		}
		Object[] credentialObject = {credentials.get("username"), credentials.get("password"), (status.equals("on")) ? true : false};
		return credentialObject;
	}
	
}
