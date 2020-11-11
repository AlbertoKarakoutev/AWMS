package com.company.awms.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.documents.File;

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
			ArrayList<File> thisLevelDocuments = documentRepo.findByAccessLevel(Character.toString(department) + Integer.toString(i));
			for(File document : thisLevelDocuments) {
				accessableDocumentIDs.add(document.getID());
			}
		}
		return accessableDocumentIDs;
	}
    
    public static DocumentRepo getRepository() {
    	return documentRepo;
    }
}
