package com.company.awms.data.employees;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepo extends MongoRepository<Employee, String> {

	public Employee findByNationalID(String nationalID);
	
	//public Employee findById(String id);

	public Employee findByEmail(String email);

	public List<Employee> findByFirstName(String firstName);

	public List<Employee> findByLastName(String lastName);

	public List<Employee> findByAccessLevel(String accessLevel);

}