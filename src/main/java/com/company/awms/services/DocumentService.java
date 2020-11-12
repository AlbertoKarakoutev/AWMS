package com.company.awms.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.documents.Doc;
import com.company.awms.data.documents.DocumentRepo;
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
    
    public boolean uploadDocument(MultipartFile file, String uploaderID) {
    	Employee uploader = null;
    	LocalDate date = LocalDate.now();
    	try {
    		uploader = EmployeeService.getRepository().findById(uploaderID).get();
    		
    	}catch(Exception e) {
    		System.out.println("User not found!");
    		return false;
    	}
    	Doc document = new Doc(uploader.getAccessLevel());
    	document.setUploaderID(uploaderID);
    	try {
			document.setData(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	document.setUploadDate(date);
    	double size = ((File) file).length();
    	document.setSize(size);
    	documentRepo.save(document);
    	return true;
    }

    // Check if documentRepo.findById(documentID) is present before calling .get().
	// If not throw an IOException and catch it in the controller and return 404 Not Found
    public Doc downloadDocument(String documentID, String downloaderID) {
    	Doc documentToDownload;
    	try {
    		documentToDownload = documentRepo.findById(documentID).get();
    	}catch(Exception e) {
    		System.out.println("Document not found!");
    		return null;
    	}
    	if(!documentToDownload.getDownloaders().contains(downloaderID)) {
    		documentToDownload.getDownloaders().add(downloaderID);
    	}
    	documentRepo.save(documentToDownload);
    	return documentToDownload;
    }
    
    public static DocumentRepo getRepository() {
    	return documentRepo;
    }
}
