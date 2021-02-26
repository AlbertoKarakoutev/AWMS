package com.company.awms.modules.base.employees.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface EmployeeRepo extends MongoRepository<Employee, String> {

	Optional<Employee> findByNationalID(String nationalID);

	Optional<Employee> findByEmail(String email);

	List<Employee> findByAccessLevel(String accessLevel);

	List<Employee> findByDepartment(String department);
	
	List<Employee> findAllByRole(String role);

	Optional<Employee> findByRole(String role);
}
