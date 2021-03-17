package com.company.awms.modules.base.forum.data;

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
	private String issuerName;
	private LocalDateTime dateTime;
	private String department;
	boolean limitedAccess;
	private int level;
	private boolean isAnswered;

	public ForumThread() {}
	
	public ForumThread(String issuerID, String body, String title, LocalDateTime time, boolean isAnswered, String issuerName, String department, int level, boolean limitedAccess) {
		this.issuerID = issuerID;
		this.issuerName = issuerName;
		this.title = title;
		this.body = body;
		this.dateTime = time;
		this.isAnswered = isAnswered;
		this.department = department;
		this.level = level;
		this.limitedAccess = limitedAccess;
	}

	public String getID() {
		return this.id;
	}

	public String getIssuerID() {
		return this.issuerID;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
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

	public String getIssuerName() {
		return issuerName;
	}

	public boolean isLimitedAccess() {
		return limitedAccess;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
