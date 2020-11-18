package com.company.awms.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.documents.Doc;
import com.company.awms.services.DocumentService;

@RestController
public class DocumentsController {

    private DocumentService documentService;

    @Autowired
    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping(value = "document/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPublicDocument(@RequestParam MultipartFile file){
        try{
            //TODO:
            //get uploaderId from current user
            documentService.uploadDocument(file, "5fa80775cb0e9c6301e92d3a");

            return new ResponseEntity<>("Successfully uploaded file!", HttpStatus.OK);
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "document/public/download/{documentId}")
    public ResponseEntity<Doc> downloadDocument(@PathVariable String documentId){
        try {
            //TODO:
            //replace downloaderId with current user Id
            //check if user has access to download document
            Doc document = documentService.downloadDocument(documentId, "5fa80775cb0e9c6301e92d3a");

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO:
    //getAllDocuments in department
    //get personal document
    //add personal document
    //delete personal document
    //delete public document
    //edit personal document
    //edit public document
}
