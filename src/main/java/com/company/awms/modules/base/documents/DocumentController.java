package com.company.awms.modules.base.documents;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.modules.base.documents.data.Doc;
import com.company.awms.modules.base.documents.data.DocInfoDTO;
import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.security.EmployeeDetails;

@Controller
@RequestMapping("/document")
public class DocumentController {

    private static final boolean active = true;

    private DocumentService documentService;
    private EmployeeService employeeService;

    @Autowired
    public DocumentController(DocumentService documentService, EmployeeService employeeService) {
        this.documentService = documentService;
        this.employeeService = employeeService;
    }

    @PostMapping(value = "/public/upload")
    public String uploadPublicDocument(@RequestParam MultipartFile file,
            @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
        if (!active) {
            return "errors/notFound";
        }

        try {
            documentService.uploadPublicDocument(file, employeeDetails.getID());

            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());
            model.addAttribute("documents", documents);
            model.addAttribute("type", "public");
            
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/documents/documents";
        } catch (IOException e) {
            return "erorrs/badRequest";
        } catch (Exception e) {
            e.printStackTrace();
            return "erorrs/internalServerError";
        }
    }

    @GetMapping(value = "/public/download/{documentID}")
    public ResponseEntity<Resource> downloadPublicDocument(@PathVariable String documentID,
            @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        try {
            Doc document = this.documentService.downloadPublicDocument(documentID, employeeDetails.getID());

            ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .header("fileName", document.getName()).contentLength(document.getData().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/personal/download/{ownerID}/{documentID}")
    public ResponseEntity<Resource> downloadPersonalDocument(@PathVariable int documentID, @PathVariable String ownerID,
            @AuthenticationPrincipal EmployeeDetails employeeDetails) {
        try {
            Doc document = documentService.downloadPersonalDocument(documentID, ownerID, employeeDetails.getID());

            ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .header("fileName", document.getName()).contentLength(document.getData().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(byteArrayResource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/public/delete/{documentID}")
    public String deletePublicDocument(@PathVariable String documentID,
            @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
        if (!active) {
            return "erorrs/notFound";
        }

        try {
            this.documentService.deletePublicDocument(documentID, employeeDetails.getID());

            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);
            model.addAttribute("type", "public");
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/documents/documents";
        } catch (IllegalArgumentException e) {
            return "erorrs/notFound";
        } catch (IllegalAccessException e) {
            return "erorrs/notAuthorized";
        } catch (Exception e) {
            e.printStackTrace();
            return "erorrs/internalServerError";
        }
    }

    @GetMapping(value = "/public")
    public String getAccessibleDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
        if (!active) {
            return "errors/notFound";
        }

        try {
            List<DocInfoDTO> documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);
            model.addAttribute("type", "public");
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/documents/documents";
        } catch (IOException e) {
            return "erorrs/notFound";
        } catch (Exception e) {
            e.printStackTrace();
            return "erorrs/internalServerError";
        }
    }

    @GetMapping(value = "/personal")
    public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
        if (!active) {
            return "errors/notFound";
        }

        try {
            List<DocInfoDTO> documents = this.documentService.getPersonalDocumentsInfo(employeeDetails.getID());

            model.addAttribute("documents", documents);
            model.addAttribute("type", "personal");
            model.addAttribute("ownerID", employeeDetails.getID());
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/documents/documents";
        } catch (Exception e) {
            e.printStackTrace();
            return "erorrs/internalServerError";
        }
    }

    @GetMapping("/public/search")
    public String searchInDocuments(@RequestParam String name, @AuthenticationPrincipal EmployeeDetails employeeDetails,
            Model model) {
        if (!active) {
            return "errors/notFound";
        }

        try {
            List<DocInfoDTO> documents;
            documents = this.documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

            List<DocInfoDTO> foundDocuments = this.documentService.searchInDocumentByName(documents, name);
            model.addAttribute("documents", foundDocuments);
            model.addAttribute("type", "search");
            injectLoggedInEmployeeInfo(model, employeeDetails);

            return "base/documents/documents";
        } catch (IOException e) {
            return "erorrs/notFound";
        } catch (Exception e) {
            return "erorrs/internalServerError";
        }
    }

    private void injectLoggedInEmployeeInfo(Model model, EmployeeDetails employeeDetails) throws IOException {
		model.addAttribute("employeeName", employeeDetails.getFirstName() + " " + employeeDetails.getLastName());
		model.addAttribute("employeeEmail", employeeDetails.getUsername());
		model.addAttribute("employeeID", employeeDetails.getID());
		Employee user = employeeService.getEmployee(employeeDetails.getID());
		int unread = 0;
		for(int i = 0; i < user.getNotifications().size(); i++) {
			if(!user.getNotifications().get(i).getRead()) {
				unread++;
			}
		}
		model.addAttribute("extModules", employeeService.getExtensionModulesDTOs());
		model.addAttribute("notifications", user.getNotifications());
		model.addAttribute("unread", unread);
	}

    public static boolean getActive() {
        return active;
    }
}
