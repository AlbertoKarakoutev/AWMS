package com.company.awms.controllers;

import java.io.IOException;
import java.util.List;

import com.company.awms.data.documents.DocInfoDTO;
import com.company.awms.data.documents.EmployeeDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<String> uploadPublicDocument(@RequestParam MultipartFile file, @RequestParam String uploaderID){
        try{
            //uploaderID can be taken from the authentication
            this.documentService.uploadPublicDocument(file, uploaderID);

            return new ResponseEntity<>("Successfully uploaded file!", HttpStatus.OK);
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "document/private/upload/{employeeID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPrivateDocument(@RequestParam MultipartFile file, @PathVariable String employeeID, @RequestParam String uploaderID){
        try{
            //uploaderID can be taken from the authentication - only admin can upload to employeeID's private documents
            this.documentService.uploadPrivateDocument(file, uploaderID, employeeID);

            return new ResponseEntity<>("Successfully uploaded document!", HttpStatus.OK);
        } catch (IOException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "document/public/download/{documentID}")
    public ResponseEntity<Doc> downloadPublicDocument(@PathVariable String documentID, @RequestParam String downloaderID){
        try {
            //downloaderID can be taken from the authentication
            Doc document = this.documentService.downloadPublicDocument(documentID, downloaderID);

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "document/private/download/{documentID}")
    public ResponseEntity<EmployeeDoc> downloadPrivateDocument(@PathVariable String documentID, @RequestParam String downloaderID){
        try {
            //downloaderID can be taken from the authentication
            EmployeeDoc document = this.documentService.downloadPrivateDocument(documentID, downloaderID);

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "documents/public/delete/{documentID}")
    public ResponseEntity<String> deletePublicDocument(@PathVariable String documentID, @RequestParam String employeeID){
        try{
            //employeeID can be taken from the authentication

            this.documentService.deletePublicDocument(documentID, employeeID);

            return new ResponseEntity<>("Successfully deleted document!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "documents/private/delete/{documentID}")
    public ResponseEntity<String> deletePrivateDocument(@PathVariable String documentID, @RequestParam String employeeID){
        try{
            //employeeID can be taken from the authentication

            this.documentService.deletePrivateDocument(documentID, employeeID);

            return new ResponseEntity<>("Successfully deleted document!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "document/public/accessible")
    public ResponseEntity<List<DocInfoDTO>> getAccessibleDocuments(@RequestParam String employeeID){
        try {
            //employeeID can be taken from the authentication
            List<DocInfoDTO> documents = this.documentService.getAllAccessibleDocumentsInfo(employeeID);

            return new ResponseEntity<>(documents, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "document/private/own")
    public ResponseEntity<List<DocInfoDTO>> getAllPrivateDocuments(@RequestParam String employeeID){
        try {
            //employeeID can be taken from the authentication
            List<DocInfoDTO> documents = this.documentService.getPrivateDocumentsInfo(employeeID);

            return new ResponseEntity<>(documents, HttpStatus.OK);
        }  catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
