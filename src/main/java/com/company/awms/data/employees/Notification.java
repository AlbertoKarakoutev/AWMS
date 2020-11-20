package com.company.awms.data.employees;

import java.time.LocalDateTime;

public class Notification {

	private String message;
	private LocalDateTime dateTime;
	private String url;
	private boolean read = false;

	public Notification() {
		this.dateTime = LocalDateTime.now();
	}

	public Notification(String message, String url) {
		this.dateTime = LocalDateTime.now();
		this.message = message;
		this.url = url;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getUrl() {
		return this.url;
	}

	public LocalDateTime getDateTime() {
		return this.dateTime;
	}

	public boolean getRead() {
		return this.read;
	}
}
