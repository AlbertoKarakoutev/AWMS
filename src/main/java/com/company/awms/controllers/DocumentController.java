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
import com.company.awms.data.employees.Employee;
import com.company.awms.services.DocumentService;
import com.company.awms.services.EmployeeService;

@RestController
public class DocumentController {

    private DocumentService documentService;
	private EmployeeService employeeService;
	
    @Autowired
    public DocumentController(DocumentService documentService, EmployeeService employeeService) {
        this.documentService = documentService;
        this.employeeService = employeeService;
    }
    
    @PostMapping(value = "document/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPublicDocument(@RequestParam MultipartFile file, @RequestParam String uploaderID){
        try{
            documentService.uploadDocument(file, uploaderID);

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
    public ResponseEntity<Doc> downloadDocument(@PathVariable String documentID, @RequestParam String downloaderID){
        try {
            Doc document = documentService.downloadDocument(documentID, downloaderID);

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "document/private/download/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPersonalDocument(@RequestParam MultipartFile file, @RequestParam String uploaderID){
        try {
        	documentService.uploadPrivateDocument(file, uploaderID);   
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
    
    @GetMapping(value = "document/private/download/{documentId}")
    public ResponseEntity<Doc> downloadPersonalDocument(@PathVariable String documentID, @RequestParam String downloaderID, @RequestParam String employeeID){
        try {
        	Doc document = documentService.downloadPrivateDocument(documentID, downloaderID, employeeID);
            return new ResponseEntity<>(document, HttpStatus.OK);            
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    //TODO:
    //getAllDocuments in department
    //delete personal document
    //delete public document
    //edit personal document
    //edit public document
}
