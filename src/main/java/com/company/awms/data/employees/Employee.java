package com.company.awms.data.employees;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//Main employee class
@Document
public class Employee {

	@Id
	public String id;

	public String nationalID;
	public String firstName;
	public String lastName;
	public String email;
	public String iban;
	public String accessLevel;
	public String phoneNumber;
	public ArrayList<Integer> rewards = new ArrayList<Integer>();
	public double salary;

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
		return nationalID;
	}

	public double getSalary() {
		return this.salary;
	}
	
	public void addReward(int reward) {
		this.rewards.add(reward);
	}
	
	public ArrayList<Integer> getRewards(){
		return this.rewards;
	}
}
