package com.company.awms

import org.springframework.data.annotation.ID;

public class Employee {
	
	@Id
	public final String id;
	
	public final int nationalID;
	public String firstName;
	public String lastName;
	public String email;
	public String iban;
	public String accessLevel;
	public int salary;
	
	public Employee(int nationalID) {
		this.nationalID = nationalID;
	}
	public Employee(String firstName, String lastName, int nationalID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nationalID = nationalID;
	}
	
	public String info() {
		return String.format("Employee: %s \n %s %s \n %s \n %s \n Access Level: %s \n IBAN: %s", id, firstName, lastName, nationalID, email, accessLevel, iban);
	}
	
	public EmployeeDailyReference createEmployeeDailyReference(String date, int[] workTime) {
		EmployeeDailyReference empDayRef = new EmployeeDailyReference(nationalID);
		empDayRef.setDate(date);
		empDayRef.setWorkTime(workTime);
		return empDayRef;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setIBAN(String iban) {
		this.iban = iban;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getEmail() {
		return this.email;
	}
	public String getIBAN() {
		return this.iban;
	}
	public String getAccessLevel() {
		return this.accessLevel;
	}
	public int getSalary() {
		return this.salary;
}