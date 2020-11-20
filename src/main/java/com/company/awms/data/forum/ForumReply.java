package com.company.awms.data.forum;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ForumReply {

	@Id
	private String id;

	private String threadID;
	private String issuerID;
	private String body;
	private LocalDateTime dateTime;
	
	public ForumReply() {}

	public ForumReply(String threadID, String issuerID, String body, LocalDateTime dateTime) {
		this.threadID = threadID;
		this.issuerID = issuerID;
		this.body = body;
		this.dateTime = dateTime;
	}

	public String getId() {
		return id;
	}

	public String getIssuerID() {
		return this.issuerID;
	}

	public LocalDateTime getDateTime() {
		return this.dateTime;
	}

	public String getThreadID() {
		return threadID;
	}

	public String getBody() {
		return body;
	}

	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
