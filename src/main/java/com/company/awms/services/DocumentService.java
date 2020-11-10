package com.company.awms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.awms.data.documents.DocumentRepo;

@Service
public class DocumentService {

    private static DocumentRepo documentRepo;

    @Autowired
    public DocumentService(DocumentRepo documentRepo) {
        DocumentService.documentRepo = documentRepo;
    }
    
    
    public static DocumentRepo getRepository() {
    	return documentRepo;
    }
}
