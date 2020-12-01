package com.company.awms.controllers;

import java.io.IOException;
import java.util.List;

import com.company.awms.security.EmployeeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.documents.Doc;
import com.company.awms.data.documents.DocInfoDTO;
import com.company.awms.services.DocumentService;

@Controller
public class DocumentController {

	private static final boolean active = true;
	
    private DocumentService documentService;
	
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping(value = "/document/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadPublicDocument(@RequestParam MultipartFile file, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try{
            documentService.uploadPublicDocument(file, employeeDetails.getID());

            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());
            model.addAttribute("documents", documents);

            return "publicDocuments";
        } catch (IOException e){
            return "badRequest";
        } catch (Exception e){
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @ResponseBody
    @GetMapping(value = "/document/public/download/{documentID}")
    public ResponseEntity<Doc> downloadPublicDocument(@PathVariable String documentID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try {
            Doc document = this.documentService.downloadPublicDocument(documentID, employeeDetails.getID());

            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @GetMapping(value = "/document/personal/download/{documentID}")
    public ResponseEntity<Doc> downloadPersonalDocument(@PathVariable int documentID, @RequestParam String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try {
        	Doc document = documentService.downloadPersonalDocument(documentID, employeeDetails.getID(), ownerID);

            return new ResponseEntity<>(document, HttpStatus.OK);            
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @DeleteMapping(value = "/document/public/delete/{documentID}")
    public ResponseEntity<String> deletePublicDocument(@PathVariable String documentID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try{
            this.documentService.deletePublicDocument(documentID, employeeDetails.getID());

            return new ResponseEntity<>("Successfully deleted document!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/document/public/accessible")
    public String getAccessibleDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try {
            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);

            return "publicDocuments";
        } catch (IOException e) {
            return "notFound";
        } catch (Exception e) {
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @GetMapping(value = "/document/personal/all")
    public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try {
            //employeeID can be taken from the authentication
            List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);

            return "personalDocuments";
        }  catch (Exception e) {
            e.printStackTrace();
            return "internalServerError";
        }
    }

	public static boolean getActive() {
		return active;
	}
}
