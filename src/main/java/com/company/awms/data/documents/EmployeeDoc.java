package com.company.awms.data.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class EmployeeDoc {

    @Id
    private String id;

    private String employeeID;
    private Doc document;

    public EmployeeDoc(String employeeID, Doc document) {
        this.employeeID = employeeID;
        this.document = document;
    }

    public String getEmployeeID() {
        return employeeID;
    }
    public Doc getDocument() {
        return document;
    }
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
    public void setDocument(Doc document) {
        this.document = document;
    }
}
