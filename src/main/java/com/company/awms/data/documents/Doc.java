package com.company.awms.data.documents;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Doc {

	@Id
	private String id;
	
	private Binary data;
	private String name;
	private String type;
	private String department;
	private int accessLevel;
	private String uploaderID;
	private LocalDateTime uploadDateTime;
	private List<String> downloaderIDs = new ArrayList<String>();
	private double size;
	
	public Doc() {
	}

	public Doc(Binary data, String name, String type, String department, int accessLevel, String uploaderID,
			   LocalDateTime uploadDateTime, double size) {
		this.data = data;
		this.name = name;
		this.type = type;
		this.department = department;
		this.accessLevel = accessLevel;
		this.uploaderID = uploaderID;
		this.uploadDateTime = uploadDateTime;
		this.size = size;
	}

	public String getID() {
		return id;
	}

	public Binary getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public String getDepartment() {
		return department;
	}

	public double getSize() {
		return size;
	}

	public String getUploaderID() {
		return this.uploaderID;
	}

	public List<String> getDownloaderIDs() {
		return downloaderIDs;
	}

	public LocalDateTime getUploadDateTime() {
		return this.uploadDateTime;
	}

	public void setData(Binary data){
		this.data = data;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public void setSize(double size) {
		this.size = size;
	}
}
