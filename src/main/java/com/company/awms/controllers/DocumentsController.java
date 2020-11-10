package com.company.awms.controllers;

import com.company.awms.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentsController {

    private DocumentService documentService;

    @Autowired
    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
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
