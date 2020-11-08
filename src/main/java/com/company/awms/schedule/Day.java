package com.example.accessingdatamongodb;

import org.springframework.data.annotation.Id;

public class Day {

  @Id
  public String id;

  public String date;
  public ArrayList<EmployeeDailyReference> employees;

  public Day(String date) {
    this.date = date;
  }

  public void addEmployee(int nationalID, int[] workTime) {
	  Employee employee = findByNationalID(nationalID);
	  EmployeeDailyReference edr= employee.createEmployeeDailyReference(date, workTime);
	  employees.add(newEmployeeDailyReference);
  }
  
}