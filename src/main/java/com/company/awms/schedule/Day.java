package com.company.awms.schedule;

import org.springframework.data.annotation.Id;

public class Day {

  @Id
  public String id;

  public String date;
  public ArrayList<EmployeeDailyReference> employees;

  // Should be in DD.MM.YYYY format
  public Day(String date) {
    this.date = date;
  }

  //Create a employee reference with apropriate information and add to the current day employyees array
  public void addEmployee(int nationalID, int[] workTime) {
	  Employee employee = findByNationalID(nationalID);
	  EmployeeDailyReference edr= employee.createEmployeeDailyReference(date, workTime);
	  employees.add(newEmployeeDailyReference);
  }
  
}