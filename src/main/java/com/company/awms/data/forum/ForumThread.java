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
	private String issuerId;
	//Should be in the YYYY-MM-DDTHH:MM:SS format
	private LocalDateTime time;
	
	public ForumThread() {}
	
	public ForumThread(String issuerId, String body, String title, LocalDateTime time) {
		this.issuerId = issuerId;
		this.title = title;
		this.body = body;
		this.time = time;
	}

	public String getID() {
		return this.id;
	}
	public String getIssuerId() {
		return this.issuerId;
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
	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
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
}
