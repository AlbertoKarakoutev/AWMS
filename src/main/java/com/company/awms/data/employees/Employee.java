package com.company.awms.data.employees;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.company.awms.data.documents.Doc;

//Main employee class
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

	@Id
	private String id;

	private String nationalID;
	private String firstName;
	private String lastName;
	private String password;
	private String role;
	private String email;
	private String iban;
	private int level;
	private String department;
	private String accessLevel;
	private String phoneNumber;
	private double salary;
	private double payPerHour;
	private int[] workWeek = new int[2];
	// Should be in the form ("start":Date, "end":Date, "paid":boolean)
	private List<Map<String, Object>> leaves = new ArrayList<>();
	private List<Notification> notifications = new ArrayList<>();
	private List<Doc> personalDocuments = new ArrayList<>();

	public Employee() {
	}

	public Employee(String nationalID) {
		this.nationalID = nationalID;
	}

	public Employee(String firstName, String lastName, String nationalID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nationalID = nationalID;
	}

	public Employee(String nationalID, String firstName, String lastName, String password, String email, String iban,
			String accessLevel, String department, int level, String phoneNumber, double salary, int[] workWeek,
			double payPerHour) {
		this.nationalID = nationalID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.iban = iban;
		this.accessLevel = accessLevel;
		this.department = department;
		this.level = level;
		this.phoneNumber = phoneNumber;
		this.salary = salary;
		this.workWeek = workWeek;
		this.payPerHour = payPerHour;
	}

	// Get employee information
	@Override
	public String toString() {
		return String.format(
				"Employee ID: %s %nName: %s %s %nNational ID: %s %nE-mail: %s %nAccess Level: %s %nDepartment: %s %nLevel: %s %nIBAN: %s %nPhone number: %s",
				id, firstName, lastName, nationalID, email, accessLevel, department, level, iban, phoneNumber);
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

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
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

	public int getLevel() {
		return this.level;
	}

	public String getDepartment() {
		return department;
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

	public double getPayPerHour() {
		return payPerHour;
	}

	public List<Map<String, Object>> getLeaves() {
		return this.leaves;
	}

	public List<Notification> getNotifications() {
		return this.notifications;
	}

	public List<Doc> getPersonalDocuments() {
		return this.personalDocuments;
	}

	public void setId(String id) { this.id = id; }

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(String role) {
		this.role = role;
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

	public void setLevel(int level) {
		this.level = level;
		setAccessLevel(department + Integer.toString(level));
	}

	public void setDepartment(String department) {
		this.department = department;
		setAccessLevel(department + Integer.toString(level));
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPayPerHour(double payPerHour) {
		this.payPerHour = payPerHour;
	}

	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
	}

	public void setWorkWeek(int[] workWeek) {
		this.workWeek = workWeek;
	}

	public void setLeaves(List<Map<String, Object>> leaves) {
		this.leaves = leaves;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public void setPersonalDocuments(List<Doc> privateDocuments) {
		this.personalDocuments = privateDocuments;
	}
}
