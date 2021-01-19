package com.company.awms.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.company.awms.data.documents.DocInfoDTO;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.data.documents.Doc;
import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;

@Service
public class DocumentService {

	private DocumentRepo documentRepo;
	private EmployeeRepo employeeRepo;

	@Autowired
	public DocumentService(DocumentRepo documentRepo, EmployeeRepo employeeRepo) {
		this.documentRepo = documentRepo;
		this.employeeRepo = employeeRepo;
	}

	private Employee getEmployee(String employeeID) throws IOException {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if (employee.isEmpty()) {
			throw new IOException("Employee with id " + employeeID + " doesn't exist");
		}

		return employee.get();
	}

	// Access to all public documents info form the same dpt. and same or lower
	// level
	public List<DocInfoDTO> getAccessibleDocumentsInfo(String employeeID) throws IOException {
		List<DocInfoDTO> accessibleDocumentsInfo = new ArrayList<>();

		Employee employee = getEmployee(employeeID);
		List<Doc> departmentDocuments;

		Optional<Employee> adminOptional = employeeRepo.findByRole("ADMIN");

		if(adminOptional.isEmpty()){
			throw new IOException("Admin not found!");
		}

		if (employee.getID().equals(adminOptional.get().getID())) {
			departmentDocuments = this.documentRepo.findAll();
			for (Doc document : departmentDocuments) {
				DocInfoDTO documentInfo = new DocInfoDTO(document.getID(), document.getName(), document.getSize(), document.getType(), document.getUploaderID(), employee.getFirstName() + " " + employee.getLastName());
				accessibleDocumentsInfo.add(documentInfo);			
			}
		} else {
			departmentDocuments = this.documentRepo.findByDepartment(employee.getDepartment());
			for (Doc document : departmentDocuments) {
				if (employee.getLevel() >= document.getLevel()) {
					DocInfoDTO documentInfo = new DocInfoDTO(document.getID(), document.getName(), document.getSize(), document.getType(), document.getUploaderID(), employee.getFirstName() + " " + employee.getLastName());
					accessibleDocumentsInfo.add(documentInfo);
				}
			}
		}

		return accessibleDocumentsInfo;
	}

	// Access to all private documents info
	public List<DocInfoDTO> getPersonalDocumentsInfo(String employeeID) throws IOException {
		List<DocInfoDTO> personalDocumentsInfo = new ArrayList<>();
		Employee employee = getEmployee(employeeID);

		List<Doc> personalDocuments = employee.getPersonalDocuments();

		for (int i = 0; i < personalDocuments.size(); i++) {
			DocInfoDTO documentInfo = new DocInfoDTO(Integer.toString(i), personalDocuments.get(i).getName(), personalDocuments.get(i).getSize(), personalDocuments.get(i).getType(), personalDocuments.get(i).getUploaderID(),
					employee.getFirstName() + " " + employee.getLastName());
			personalDocumentsInfo.add(documentInfo);
		}

		return personalDocumentsInfo;
	}

	private Doc createNewDoc(MultipartFile file, String ownerID, Employee owner) throws IOException {
		Binary data = new Binary(BsonBinarySubType.BINARY, file.getBytes());
		String fileName = file.getOriginalFilename();
		String fileType = file.getContentType();
		String department = owner.getDepartment();
		int level = owner.getLevel();
		long fileSize = file.getSize();
		LocalDateTime dateTime = LocalDateTime.now();

		return new Doc(data, level, department, fileName, fileType, ownerID, dateTime, fileSize);
	}

	public void uploadPublicDocument(MultipartFile file, String uploaderID) throws IOException {
		Employee uploader = getEmployee(uploaderID);

		Doc document = createNewDoc(file, uploaderID, uploader);

		this.documentRepo.save(document);
	}

	// only the admin can upload personal docs
	public void uploadPersonalDocument(MultipartFile file, String ownerID) throws IOException {
		Employee owner = getEmployee(ownerID);

		Doc document = createNewDoc(file, ownerID, owner);

		owner.getPersonalDocuments().add(document);
		this.employeeRepo.save(owner);
	}

	public Doc downloadPublicDocument(String documentID, String downloaderID) throws IOException, IllegalAccessException {
		Optional<Doc> documentToDownloadOptional = this.documentRepo.findById(documentID);

		if (documentToDownloadOptional.isEmpty()) {
			throw new IOException("Document not found!");
		}

		Doc documentToDownload = documentToDownloadOptional.get();

		if (!isAccessible(documentToDownload.getDepartment(), documentToDownload.getLevel(), downloaderID)) {
			throw new IllegalAccessException("Document not accessible!");
		}

		documentToDownload.getDownloaderIDs().add(downloaderID);
		this.documentRepo.save(documentToDownload);

		return documentToDownload;
	}

	// Both Employee and Admin can download a personal document
	public Doc downloadPersonalDocument(int documentID, String ownerID, String downloaderID) throws IOException, IllegalAccessException {
		Employee owner = getEmployee(ownerID);

		Optional<Employee> adminOptional = employeeRepo.findByRole("ADMIN");

		if(adminOptional.isEmpty()){
			throw new IOException("Admin not found!");
		}
		if (!downloaderID.equals(adminOptional.get().getID()) && !downloaderID.equals(ownerID)) {
			throw new IllegalAccessException("Document not accessible!");
		}

		List<Doc> personalDocuments = owner.getPersonalDocuments();

		if (personalDocuments.size() <= documentID) {
			throw new IOException("Document with id " + documentID + " doesn't exist");
		}

		return personalDocuments.get(documentID);
	}

	public void deletePublicDocument(String documentID, String employeeID) throws IOException, IllegalAccessException {
		Optional<Doc> documentToDelete = this.documentRepo.findById(documentID);

		if (documentToDelete.isEmpty()) {
			throw new IOException("Document not found!");
		}

		Optional<Employee> adminOptional = employeeRepo.findByRole("ADMIN");

		if(adminOptional.isEmpty()){
			throw new IOException("Admin not found!");
		}

		if (employeeID.equals(adminOptional.get().getID()) || documentToDelete.get().getUploaderID().equals(employeeID)) {
			this.documentRepo.deleteById(documentID);
		} else {
			throw new IllegalAccessException("You don't have permission to delete document");
		}
	}

	// only admin can delete private documents
	public void deletePersonalDocument(int documentID, String ownerID) throws IOException {
		Employee owner = getEmployee(ownerID);

		List<Doc> personalDocuments = owner.getPersonalDocuments();

		if (personalDocuments.size() <= documentID) {
			throw new IllegalArgumentException("Document with id " + documentID + " doesn't exists");
		}

		personalDocuments.remove(documentID);

		this.employeeRepo.save(owner);
	}

	public List<DocInfoDTO> searchInDocumentByName(List<DocInfoDTO> documents, String name) {
		List<DocInfoDTO> foundDocuments = new ArrayList<>();

		Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

		for (DocInfoDTO document : documents) {
			Matcher matcher = pattern.matcher(document.getName());
			boolean matchFound = matcher.find();
			if (matchFound) {
				foundDocuments.add(document);
			}
		}
		return foundDocuments;
	}

	private boolean isAccessible(String department, int level, String employeeID) throws IOException {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if (employee.isEmpty()) {
			return false;
		}

		Optional<Employee> adminOptional = employeeRepo.findByRole("ADMIN");

		if(adminOptional.isEmpty()){
			throw new IOException("Admin not found!");
		}

		// The Admin always has access
		if (employeeID.equals(adminOptional.get().getID())) {
			return true;
		} else {
			return department.equals(employee.get().getDepartment()) && level <= employee.get().getLevel();
		}
	}
}
