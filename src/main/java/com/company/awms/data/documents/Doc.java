package com.company.awms.data.documents;

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
	private LocalDateTime uploadDateTime;
	private Set<String> downloaderIDs = new HashSet<>();
	private double size;
	
	public Doc() {
	}

	public Doc(Binary data, int level, String department, String name, String type, String uploaderID,
			   LocalDateTime uploadDateTime, double size) {
		this.data = data;
		this.level = level;
		this.department = department;
		this.name = name;
		this.type = type;
		this.uploaderID = uploaderID;
		this.uploadDateTime = uploadDateTime;
		this.size = size;
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

	public double getSize() {
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

	public String getUploader() {
		return this.uploaderID;
	}

	public Set<String> getDownloaderIDs() {
		return this.downloaderIDs;
	}

	public LocalDateTime getUploadDateTime() {
		return this.uploadDateTime;
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

	public void setDownloaderIDs(Set<String> downloaderIDs) {
		this.downloaderIDs = downloaderIDs;
	}

	public void setSize(double size) {
		this.size = size;
	}
}
