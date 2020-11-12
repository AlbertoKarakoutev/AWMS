package com.company.awms.data.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ForumReply {

	@Id
	private String id;

	private String threadID;
	private String issuerID;
	private String body;
	private LocalDateTime time;
	
	public ForumReply() {}

	public ForumReply(String threadID, String issuerID, String body, LocalDateTime time) {
		this.threadID = threadID;
		this.issuerID = issuerID;
		this.body = body;
		this.time = time;
	}

	public String getId() {
		return id;
	}
	public String getIssuerID() {
		return this.issuerID;
	}
	public LocalDateTime getTime() {
		return this.time;
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
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
