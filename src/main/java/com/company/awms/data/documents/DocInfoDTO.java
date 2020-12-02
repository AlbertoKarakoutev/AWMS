package com.company.awms.data.documents;

public class DocInfoDTO {
    private String name;
    private double size;
    private String type;
    private String ownerID;

    public DocInfoDTO(String name, double size, String type, String ownerID) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.ownerID = ownerID;
    }

    public String getName() {
        return name;
    }

    public double getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}