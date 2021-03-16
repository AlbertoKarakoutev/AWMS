package com.company.awms.modules.base.employees.data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.company.awms.modules.base.employees.EmployeeService;

public class Notification {

	private String message;
	private LocalDateTime dateTime;
	private List<Object> data;
	private boolean read = false;

	private static String senderUsername;
	private static String senderPassword;
	private static boolean emailNotifications;
	
	public Notification() {}
	
	public Notification(String message) {
		this.dateTime = LocalDateTime.now();
		this.message = message;
		data = new ArrayList<Object>();
	}
	
	public Notification add(Object o){
		data.add(o);
		return this;
	}
	
	public void sendAndSave(Employee notified, EmployeeRepo employeeRepo) {
		notified.getNotifications().add(this);
		employeeRepo.save(notified);
		if(emailNotifications) {
			String address = null;
			try {
				address =  "https://"+InetAddress.getLocalHost().getHostAddress()+":8443";
			}catch(UnknownHostException e) {}
			EmailNotifier.sendMail("smtp.gmail.com", senderUsername, senderPassword, notified.getEmail(), "AWMS Notification", message + "</br>View it at <a href='"+address+"'>AWMS</a>");
		}
	}
	
	public void sendAndSave(String employeeID, EmployeeRepo employeeRepo) throws IOException {
		Optional<Employee> notified = employeeRepo.findById(employeeID);
		if(notified.isEmpty()) {
			throw new IOException();
		}
		sendAndSave(notified.get(), employeeRepo);
	}
	
	public static void setAsRead(EmployeeService employeeService, String employeeID, int notificationNumber) throws IOException {
		Optional<Employee> employee = employeeService.getRepo().findById(employeeID);
		if (employee.isEmpty()) {
			throw new IOException("Employee not found!");
		} else {
			employee.get().getNotifications().get(notificationNumber).setRead(true);
			employeeService.getRepo().save(employee.get());
		}
	}
	
	public static void setCredentials(String username, String password, boolean on) {
		senderUsername = username;
		senderPassword = password;
		emailNotifications = on;
	}
	
	public static Object[] getCredentials() {
		if(senderUsername == null)senderUsername = "";
		if(senderPassword == null)senderPassword = "";
		Object[] credentials = {senderUsername, senderPassword, emailNotifications};
		return credentials;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public void setUrl(List<Object> data) {
		this.data = data;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getMessage() {
		return this.message;
	}
	public List<Object> getData() {
		return this.data;
	}
	public LocalDateTime getDateTime() {
		return this.dateTime;
	}
	public boolean getRead() {
		return this.read;
	}

	
	
}
