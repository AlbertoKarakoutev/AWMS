package com.company.awms.data.forum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ThreadReply {
	
	ForumRepo forumRepo;
	@Id
	public String id;
	
	public ForumThread superThread;
	public String issuerID;
	public String replyBody;
	
	//Should be in the HH:MM:SS-DD.MM.YYYY format
	public LocalDateTime time;
	
	public ThreadReply() {}
	
//	public ThreadReply(String superThreadID) {
//		this.superThread = forumRepo.findByID(superThreadID);
//	}
//	public ThreadReply(String superThreadID, String issuerID, String replyBody, String time) {
//		this.superThread = forumRepo.findByID(superThreadID);
//		this.issuerID = issuerID;
//		this.replyBody = replyBody;
//		this.time = time;
//	}
	
	public String getID() {
		return this.id;
	}
	public String getIssuerID() {
		return this.issuerID;
	}
	public LocalDateTime getTime() {
		return this.time;
	}
	public String getReplyBody() {
		return this.replyBody;
	}
//	public void setSuperThread(String superThreadID) {
//		this.superThread = forumRepo.findByID(superThreadID);
//	}
	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public void setBody(String replyBody) {
		this.replyBody = replyBody;
	}
	
}
