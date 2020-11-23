package com.company.awms.data.documents;

public class DocInfoDTO {
    private String name;
    private double size;
    private String type;

    public DocInfoDTO(String name, double size, String type) {
        this.name = name;
        this.size = size;
        this.type = type;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }
}
