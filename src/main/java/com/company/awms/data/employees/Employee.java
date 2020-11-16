package com.company.awms.data.employees;

import java.util.ArrayList;
import java.util.Dictionary;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//Main employee class
@Document
public class Employee {

	@Id
	private String id;

	private String nationalID;
	private String firstName;
	private String lastName;
	private String email;
	private String iban;
	//Should be in the form %c%d, where %c is a department of the company, and %d is vertical access level in that department
	private String accessLevel;
	private String phoneNumber;
	private double salary;
	private int[] workWeek = new int[2];
	//Should be in the form ("Start":Date, "End":Date, "Paid":boolean)
	private ArrayList<Dictionary<String, Object>> leaves = new ArrayList<>();

	public Employee() {}

	public Employee(String nationalID) {
		this.nationalID = nationalID;
	}

	public Employee(String firstName, String lastName, String nationalID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nationalID = nationalID;
	}

	// Get employee information
	public String info() {
		return String.format("Employee ID: %s \nName: %s %s \nNational ID: %s \nE-mail: %s \nAccess Level: %s \nIBAN: %s\nPhone number: %s", id, firstName,
				lastName, nationalID, email, accessLevel, iban, phoneNumber);
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

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
	}
	
	public void setWorkWeek(int[] workWeek) {
		this.workWeek = workWeek;
	}
	
	public void setLeaves(ArrayList<Dictionary<String, Object>> leaves) {
		this.leaves = leaves;
	}

	public String getID() {
		return this.id;
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

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public String getNationalID() {
		return this.nationalID;
	}

	public double getSalary() {
		return this.salary;
	}
	
	public int[] getWorkWeek() {
		return this.workWeek;
	}
	
	public ArrayList<Dictionary<String, Object>> getLeaves(){
		return this.leaves;
	}
}
