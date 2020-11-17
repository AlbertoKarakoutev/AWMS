package com.company.awms.data.employees;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepo extends MongoRepository<Employee, String> {

	public Employee findByNationalID(String nationalID);

	public Employee findByEmail(String email);

	public ArrayList<Employee> findByFirstName(String firstName);

	public ArrayList<Employee> findByLastName(String lastName);

	public ArrayList<Employee> findByAccessLevel(String accessLevel);

}
