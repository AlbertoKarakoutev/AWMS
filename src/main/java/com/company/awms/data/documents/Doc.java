package com.company.awms.data.documents;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Doc {

	@Id
	private String id;
	
	private Binary data;
	private String accessLevel;
	private String uploaderID;
	private LocalDateTime uploadDateTime;
	private ArrayList<String> downloaderIDs = new ArrayList<String>();
	private double size;
	
	public Doc() {
	}
	
	public Doc(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	
	public Binary getData() {
		return data;
	}
	public String getID() {
		return id;
	}
	public String getAccessLevel() {
		return accessLevel;
	}
	public double getSize() {
		return size;
	}
	public String getUploader() {
		return this.uploaderID;
	}
	public ArrayList<String> getDownloaders() {
		return this.downloaderIDs;
	}
	public LocalDateTime getUploadDateTime() {
		return this.uploadDateTime;
	}
	public void setData(Binary data){
		this.data = data;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public void setUploaderID(String uploaderID) {
		this.uploaderID = uploaderID;
	}
	public void setUploadDateTime(LocalDateTime uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}
	public void setDownloaderIDs(ArrayList<String> downloaderIDs) {
		this.downloaderIDs = downloaderIDs;
	}
	public void setSize(double size) {
		this.size = size;
	}
}
