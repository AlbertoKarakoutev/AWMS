package com.company.awms.data.employees;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;


public interface EmployeeRepo extends MongoRepository<Employee, String> {

	Optional<Employee> findByNationalID(String nationalID);

	Optional<Employee> findByEmail(String email);

	List<Employee> findByFirstName(String firstName);

	List<Employee> findByLastName(String lastName);

	List<Employee> findByAccessLevel(String accessLevel);
	
	List<Employee> findByLevel(String level);

	List<Employee> findAllByRole(String role);

	Optional<Employee> findByRole(String role);
}
