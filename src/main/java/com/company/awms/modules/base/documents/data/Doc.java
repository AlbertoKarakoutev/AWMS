package com.company.awms.modules.base.documents.data;

import java.time.LocalDateTime;
import java.util.*;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Doc {

	@Id
	private String id;
	
	private Binary data;
	private int level;
	private String department;
	private String name;
	private String type;
	private String uploaderID;
	private boolean limitedAccess;
	private LocalDateTime uploadDateTime;
	private List<String> downloaderIDs = new ArrayList<>();
	private long size;
	
	public Doc() {
	}

	public Doc(Binary data, int level, String department, String name, String type, String uploaderID,
			   LocalDateTime uploadDateTime, long size, boolean limitedAccess) {
		this.data = data;
		this.level = level;
		this.department = department;
		this.name = name;
		this.type = type;
		this.uploaderID = uploaderID;
		this.uploadDateTime = uploadDateTime;
		this.size = size;
		this.limitedAccess = limitedAccess;
	}

	public Doc(int accessLevel) {
		this.level = accessLevel;
	}
	
	public Binary getData() {
		return data;
	}

	public String getID() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public long getSize() {
		return size;
	}

	public String getDepartment() {
		return department;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUploaderID() {
		return uploaderID;
	}

	public List<String> getDownloaderIDs() {
		return this.downloaderIDs;
	}

	public LocalDateTime getUploadDateTime() {
		return this.uploadDateTime;
	}

	public boolean isLimitedAccess() {
		return this.limitedAccess;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setData(Binary data){
		this.data = data;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUploaderID(String uploaderID) {
		this.uploaderID = uploaderID;
	}

	public void setUploadDateTime(LocalDateTime uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}

	public void setDownloaderIDs(List<String> downloaderIDs) {
		this.downloaderIDs = downloaderIDs;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
