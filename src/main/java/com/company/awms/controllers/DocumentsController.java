package com.company.awms.controllers;

import com.company.awms.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentsController {

    private DocumentService documentService;

    @Autowired
    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    //Test method
    @GetMapping("upload")
    public ResponseEntity<String> uploadDocument(){
        documentService.uploadDocument("documentation/Architecture.docx", "1234567890");
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
    
    //Test method
    @GetMapping("download")
    public ResponseEntity<String> downloadDocument(){
        documentService.downloadDocument("5fabdb4582227516d1e9e219", "1234567890");
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
    
  /*@Autowired
  DocumentRepo documentRepo;

    @GetMapping("/document")
    public ResponseEntity<String> addDocument(){
        this.documentRepo.save(new File());
        this.documentRepo.findById("дасд").
        return new ResponseEntity<>("asdads", HttpStatus.OK);
    }*/
}
