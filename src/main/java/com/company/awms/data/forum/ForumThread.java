package com.company.awms.data.forum;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ForumThread {
	
	@Id
	private String id;

	private String title;
	private String body;
	private String issuerID;
	
	public ForumThread() {}
	
	private LocalDateTime dateTime;
	private boolean isAnswered;
	
	public ForumThread(String issuerID, String body, String title, LocalDateTime time, boolean isAnswered) {
		this.issuerID = issuerID;
		this.title = title;
		this.body = body;
		this.dateTime = time;
		this.isAnswered = isAnswered;
	}

	public String getID() {
		return this.id;
	}
	public String getIssuerID() {
		return this.issuerID;
	}
	public LocalDateTime getDateTime() {
		return this.dateTime;
	}
	public String getTitle() {
		return this.title;
	}
	public String getBody() {
		return this.body;
	}
	public boolean getAnswered() {
		return isAnswered;
	}
	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void setAnswered(boolean answered) {
		isAnswered = answered;
	}
}
