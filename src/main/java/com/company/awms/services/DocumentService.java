package com.company.awms.services;

import java.io.File;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.documents.Doc;
import com.company.awms.data.employees.Employee;

@Service
public class DocumentService {

    private static DocumentRepo documentRepo;

    @Autowired
    public DocumentService(DocumentRepo documentRepo) {
        DocumentService.documentRepo = documentRepo;
    }
    
    //Access to all documents form the same dpt. and same or lower level
    public ArrayList<String> getAccessableDocumentIDs(String accessLevel) {
		ArrayList<String> accessableDocumentIDs = new ArrayList<>();
		int level = 0;
		@SuppressWarnings("null")
		char department = (Character) null;
		try {
			department = accessLevel.charAt(0);
			level = Integer.parseInt(String.valueOf(accessLevel.charAt(1)));		
		}catch(Exception e) {
			System.out.println("Error retrieving access level!");
			return null;
		}
		for(int i = 0; i <= level; i++) {
			ArrayList<Doc> thisLevelDocuments = documentRepo.findByAccessLevel(Character.toString(department) + Integer.toString(i));
			for(Doc document : thisLevelDocuments) {
				accessableDocumentIDs.add(document.getID());
			}
		}
		return accessableDocumentIDs;
	}
    
    public void uploadDocument(String path, String uploaderNationalID) {
    	Employee uploader = null;
    	LocalDate date = LocalDate.now();
    	try {
    		uploader = EmployeeService.getRepository().findByNationalID(uploaderNationalID);
    		
    	}catch(Exception e) {
    		System.out.println("User not found!");
    		return;
    	}
    	Doc document = new Doc(path, uploader.getAccessLevel());
    	document.setUploaderID(uploaderNationalID);
    	document.setUploadDate(date);
    	double size = new File(path).length();
    	document.setSize(size);
    	documentRepo.save(document);
    }

    // Check if documentRepo.findById(documentID) is present before calling .get().
	// If not throw an IOException and catch it in the controller and return 404 Not Found
    public Doc downloadDocument(String documentID, String downloaderNationalID) {
    	Doc documentToDownload;
    	try {
    		documentToDownload = documentRepo.findById(documentID).get();
    	}catch(Exception e) {
    		System.out.println("Document not found!");
    		return null;
    	}
    	if(!documentToDownload.getDownloaders().contains(downloaderNationalID)) {
    		documentToDownload.getDownloaders().add(downloaderNationalID);
    	}
    	documentRepo.save(documentToDownload);
    	System.out.println(documentToDownload.getAccessLevel());
    	return documentToDownload;
    }
    
    public static DocumentRepo getRepository() {
    	return documentRepo;
    }
}
