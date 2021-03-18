package com.company.awms.modules.base.documents;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import com.company.awms.security.EmployeeDetails;

@Controller
@RequestMapping("/document")
public class DocumentController {

	private DocumentService documentService;
	private EmployeeService employeeService;

	@Autowired
	public DocumentController(DocumentService documentService, EmployeeService employeeService) {
		this.documentService = documentService;
		this.employeeService = employeeService;
	}

	@PostMapping(value = "/public/upload")
	public String uploadPublicDocument(@RequestParam MultipartFile file, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam boolean limitedAccess) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			documentService.uploadPublicDocument(file, employeeDetails.getID(), limitedAccess);
			return "redirect:/document/public";
		} catch (IOException e) {
			return "errors/badRequest";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@PostMapping(value = "/public/delete/{documentID}")
	public String deletePublicDocument(@PathVariable String documentID, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			documentService.deletePublicDocument(documentID, employeeDetails.getID());

			return "redirect:/document/public";
		} catch (IllegalArgumentException e) {
			return "errors/notFound";
		} catch (IllegalAccessException e) {
			return "errors/notAuthorized";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}
	
	@GetMapping(value = "/public")
	public String getAccessibleDocumentsByPage(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model, @RequestParam Optional<Integer> page) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<DocInfoDTO> documents = documentService.getAccessibleDocumentsInfoByPage(employeeDetails.getID(), 1);
			
			if(page.isPresent()) {
				documents = documentService.getAccessibleDocumentsInfoByPage(employeeDetails.getID(), page.get());
				model.addAttribute("page", page.get());
			}else {
				model.addAttribute("page", 1);
			}

			model.addAttribute("pageCount", (int)Math.ceil((double)documentService.getAccessibleDocumentsInfo(employeeDetails.getID()).size()/10));
			model.addAttribute("documents", documents);
			model.addAttribute("type", "public");

			return "base/documents/documents";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping(value = "/personal")
	public String getAllPersonalDocuments(@AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<DocInfoDTO> documents = documentService.getPersonalDocumentsInfo(employeeDetails.getID());

			model.addAttribute("documents", documents);
			model.addAttribute("type", "personal");
			model.addAttribute("ownerID", employeeDetails.getID());

			return "base/documents/documents";
		} catch (Exception e) {
			e.printStackTrace();
			return "errors/internalServerError";
		}
	}

	@GetMapping("/public/search")
	public String searchInDocuments(@RequestParam String name, @AuthenticationPrincipal EmployeeDetails employeeDetails, Model model) {
		employeeService.injectLoggedInEmployeeInfo(model, employeeDetails);
		try {
			List<DocInfoDTO> documents = documentService.getAccessibleDocumentsInfo(employeeDetails.getID());

			List<DocInfoDTO> foundDocuments = documentService.searchInDocumentByName(documents, name);
			model.addAttribute("documents", foundDocuments);
			model.addAttribute("type", "search");

			return "base/documents/documents";
		} catch (IOException e) {
			return "errors/notFound";
		} catch (Exception e) {
			return "errors/internalServerError";
		}
	}

	@GetMapping(value = "/public/download/{documentID}")
	public ResponseEntity<Resource> downloadPublicDocument(@PathVariable String documentID, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			Doc document = documentService.downloadPublicDocument(documentID, employeeDetails.getID());

			ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"").header("fileName", document.getName()).contentLength(document.getData().length())
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
	public ResponseEntity<Resource> downloadPersonalDocument(@PathVariable int documentID, @PathVariable String ownerID, @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		try {
			Doc document = documentService.downloadPersonalDocument(documentID, ownerID, employeeDetails.getID());

			ByteArrayResource byteArrayResource = new ByteArrayResource(document.getData().getData());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"").header("fileName", document.getName()).contentLength(document.getData().length())
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
	
}
