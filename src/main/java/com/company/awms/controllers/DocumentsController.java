package com.company.awms.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
    
    @PostMapping("upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file, Model model)throws IOException{
        documentService.uploadDocument(file, "1234567890");
        return new ResponseEntity<>("redirect:/photos/", HttpStatus.OK);
    }
    
    @GetMapping("download/{id}")
    public ResponseEntity<Doc> downloadDocument(@PathVariable String id, Model model){
        Doc downloaded = documentService.downloadDocument(id, "1234567890");
        //model.addAttribute("data", downloaded.getData());
        return new ResponseEntity<>(downloaded, HttpStatus.OK);
    }
}
