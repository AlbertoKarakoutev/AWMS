package com.company.awms.data.documents;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class EmployeeDoc {

    @Id
    private String id;

    private Binary data;
    private String name;
    private String type;
    private String uploaderID;
    private LocalDateTime uploadDateTime;
    private double size;

    public EmployeeDoc() {
    }

    public EmployeeDoc(Binary data, String name, String type, String uploaderID,
               LocalDateTime uploadDateTime, double size) {
        this.data = data;
        this.name = name;
        this.type = type;
        this.uploaderID = uploaderID;
        this.uploadDateTime = uploadDateTime;
        this.size = size;
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

    public String getUploaderID() {
        return uploaderID;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public double getSize() {
        return size;
    }

    public void setData(Binary data) {
        this.data = data;
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

    public void setSize(double size) {
        this.size = size;
    }
}
