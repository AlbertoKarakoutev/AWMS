package com.company.awms.data.documents;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class File {

	@Id
	public String id;
	
	public String path;
	public String accessLevel;
	public String uploaderID;
	public String uploadDate;
	public ArrayList<String> downloaderIDs = new ArrayList<String>();
	public double size;
	
	public File() {
	}
	
	public File(String path, String accessLevel) {
		this.path = path;
		this.accessLevel = accessLevel;
	}
	
	public String getPath() {
		return path;
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
	public String getUploadDate() {
		return this.uploadDate;
	}
	public void setPath(String path){
		this.path = path;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	
}
