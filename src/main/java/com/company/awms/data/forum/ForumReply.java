package com.company.awms.data.forum;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumReply {

	@Id
	private String id;

	private String threadID;
	private String issuerID;
	private String issuerName;
	private String body;
	@JsonIgnore
	private LocalDateTime dateTime;
	
	public ForumReply() {}

	public ForumReply(String threadID, String issuerID, String body, LocalDateTime dateTime, String issuerName) {
		this.threadID = threadID;
		this.issuerID = issuerID;
		this.body = body;
		this.dateTime = dateTime;
		this.issuerName = issuerName;
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

	public String getIssuerName() {
		return issuerName;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
}
