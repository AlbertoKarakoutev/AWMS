package com.example.accessingdatemongodb;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepo extends MongoRepository<Employee, String, Integer> {

  public Employee findByNationalID(Integer nationalID);
  public Employee findByEmail(String email);
  public List<Employee> findByFirstName(String firstName);
  public List<Employee> findByLastName(String lastName);
  public List<Employee> findByAccessLevels(String accessLevel);

}