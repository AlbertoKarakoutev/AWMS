package com.company.awms.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.documents.Doc;
import com.company.awms.data.documents.DocInfoDTO;
import com.company.awms.security.EmployeeDetails;
import com.company.awms.services.DocumentService;

@Controller
@RequestMapping("/document")
public class DocumentController {

	private static final boolean active = true;
	
    private DocumentService documentService;
	
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping(value = "/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadPublicDocument(@RequestParam MultipartFile file, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try{
            documentService.uploadPublicDocument(file, employeeDetails.getID());

            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());
            model.addAttribute("documents", documents);
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "publicDocuments";
        } catch (IOException e){
            return "badRequest";
        } catch (Exception e){
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @GetMapping(value = "/public/download/{documentID}")
    public ResponseEntity<Resource> downloadPublicDocument(@PathVariable String documentID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try {
            Doc document = this.documentService.downloadPublicDocument(documentID, employeeDetails.getID());

            ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .contentLength(document.getData().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayResource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/personal/download/{documentID}")
    public ResponseEntity<Resource> downloadPersonalDocument(@PathVariable int documentID, @RequestParam String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try {
        	Doc document = documentService.downloadPersonalDocument(documentID, employeeDetails.getID(), ownerID);

            ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .contentLength(document.getData().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayResource);
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
    @DeleteMapping(value = "/public/delete/{documentID}")
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

    @GetMapping(value = "/public/accessible")
    public String getAccessibleDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try {
            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "publicDocuments";
        } catch (IOException e) {
            return "notFound";
        } catch (Exception e) {
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @GetMapping(value = "/personal/all")
    public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model){
        try {
            //employeeID can be taken from the authentication
            List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "personalDocuments";
        }  catch (Exception e) {
            e.printStackTrace();
            return "internalServerError";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<DocInfoDTO>> searchInDocuments(@RequestParam String name, @AuthenticationPrincipal EmployeeDetails employeeDetails){
        try {
            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            List<DocInfoDTO> foundDocuments = this.documentService.searchInDocumentByName(documents, name);

            return new ResponseEntity<>(foundDocuments, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails){
        model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
        model.addAttribute("employeeEmail", employeeDetails.getUsername());
        model.addAttribute("employeeID", employeeDetails.getID());
    }

	public static boolean getActive() {
		return active;
	}
}
