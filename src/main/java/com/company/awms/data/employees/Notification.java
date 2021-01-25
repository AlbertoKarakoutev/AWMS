package com.company.awms.data.employees;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

public class Notification {

	private String message;
	private LocalDateTime dateTime;
	private List<Object> data;
	private boolean read = false;

	public Notification() {
		this.dateTime = LocalDateTime.now();
	}

	public Notification(String message, List<Object> data) {
		this.dateTime = LocalDateTime.now();
		this.message = message;
		this.data = data;
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
