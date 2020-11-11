package com.company.awms.data.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ForumReply {

	@Id
	private String id;
	private String threadId;
	private String issuerId;
	private String body;
	//Should be in the YYYY-MM-DDTHH:MM:SS format
	private LocalDateTime time;
	
	public ForumReply() {}

	public ForumReply(String threadId, String issuerId, String body, LocalDateTime time) {
		this.threadId = threadId;
		this.issuerId = issuerId;
		this.body = body;
		this.time = time;
	}

	public String getId() {
		return id;
	}
	public String getIssuerId() {
		return this.issuerId;
	}
	public LocalDateTime getTime() {
		return this.time;
	}
	public String getThreadId() {
		return threadId;
	}
	public String getBody() {
		return body;
	}
	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
