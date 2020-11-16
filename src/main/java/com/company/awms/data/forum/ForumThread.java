package com.company.awms.data.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ForumThread {
	
	@Id
	private String id;

	private String title;
	private String body;
	private String issuerID;
	private LocalDateTime time;
	private boolean isAnswered;
	
	public ForumThread() {}
	
	public ForumThread(String issuerID, String body, String title, LocalDateTime time, boolean isAnswered) {
		this.issuerID = issuerID;
		this.title = title;
		this.body = body;
		this.time = time;
		this.isAnswered = isAnswered;
	}

	public String getID() {
		return this.id;
	}
	public String getIssuerID() {
		return this.issuerID;
	}
	public LocalDateTime getTime() {
		return this.time;
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
	public void setTime(LocalDateTime time) {
		this.time = time;
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
