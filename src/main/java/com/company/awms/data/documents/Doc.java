package com.company.awms.data.documents;

import java.time.LocalDate;
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
	private String uploaderNationalID;
	private LocalDate uploadDate;
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
		return this.uploaderNationalID;
	}
	public ArrayList<String> getDownloaders() {
		return this.downloaderIDs;
	}
	public LocalDate getUploadDate() {
		return this.uploadDate;
	}
	public void setData(Binary data){
		this.data = data;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public void setUploaderID(String uploaderNationalID) {
		this.uploaderNationalID = uploaderNationalID;
	}
	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}
	public void setDownloaderIDs(ArrayList<String> downloaderIDs) {
		this.downloaderIDs = downloaderIDs;
	}
	public void setSize(double size) {
		this.size = size;
	}
}
