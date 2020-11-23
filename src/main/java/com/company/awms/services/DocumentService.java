package com.company.awms.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	private static final String ADMIN_ID = "adminID";

	private DocumentRepo documentRepo;
	private EmployeeRepo employeeRepo;

	@Autowired
	public DocumentService(DocumentRepo documentRepo, EmployeeRepo employeeRepo) {
		this.documentRepo = documentRepo;
		this.employeeRepo = employeeRepo;
	}

	//Access to all public documents info form the same dpt. and same or lower level
	public List<DocInfoDTO> getAllAccessibleDocumentsInfo(String employeeID) throws IOException {
		List<DocInfoDTO> accessibleDocumentsInfo = new ArrayList<>();

		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if(employee.isEmpty()){
			throw new IOException("Employee with id " + employeeID + " doesn't exist");
		}

		List<Doc> departmentDocuments = this.documentRepo.findByDepartment(employee.get().getDepartment());

		for (Doc document : departmentDocuments) {
			if (employee.get().getAccessLevel() >= document.getAccessLevel()) {
				DocInfoDTO documentInfo = new DocInfoDTO(document.getName(), document.getSize(), document.getType());
				accessibleDocumentsInfo.add(documentInfo);
			}
		}

		return accessibleDocumentsInfo;
	}

	//Access to all private documents info
	public List<DocInfoDTO> getPersonalDocumentsInfo(String employeeID) {
		List<DocInfoDTO> privateDocumentsInfo = new ArrayList<>();
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if(employee.isEmpty()) {
			throw new IllegalArgumentException("Employee with id " + employeeID + " doesn't exist");
		}

		for (Doc document : employee.get().getPersonalDocuments()) {
			DocInfoDTO documentInfo = new DocInfoDTO(document.getName(), document.getSize(), document.getType());
			privateDocumentsInfo.add(documentInfo);
		}

		return privateDocumentsInfo;
	}

	public void uploadPublicDocument(MultipartFile file, String uploaderID) throws IOException {
		Optional<Employee> uploader = this.employeeRepo.findById(uploaderID);

		if(uploader.isEmpty()) {
			throw new IllegalArgumentException("Employee with id " + uploaderID + " doesn't exist");
		}

		Binary data = new Binary(BsonBinarySubType.BINARY, file.getBytes());
		String fileName = file.getOriginalFilename();
		String fileType = file.getContentType();
		String department = uploader.get().getDepartment();
		int accessLevel = uploader.get().getAccessLevel();
		LocalDateTime dateTime = LocalDateTime.now();

		Doc document = new Doc(data, accessLevel, department, fileName, fileType, uploaderID, dateTime, file.getSize());

		this.documentRepo.save(document);
	}

	//only the admin can upload the private Docs
	public void uploadPersonalDocument(MultipartFile file, String uploaderID, String ownerID) throws IOException, IllegalAccessException {
		Optional<Employee> owner = this.employeeRepo.findById(ownerID);

		if(owner.isEmpty()) {
			throw new IOException("Employee with id " + ownerID + " doesn't exist");
		}

		if(uploaderID.equals(ADMIN_ID)){
			Binary data = new Binary(BsonBinarySubType.BINARY, file.getBytes());
			String fileName = file.getOriginalFilename();
			String fileType = file.getContentType();
			LocalDateTime dateTime = LocalDateTime.now();

			Doc document = new Doc(data, owner.get().getAccessLevel(), owner.get().getDepartment(), fileName, fileType, ownerID, dateTime, file.getSize());

			owner.get().getPersonalDocuments().add(document);
			this.employeeRepo.save(owner.get());
		} else {
			throw new IllegalAccessException("You don't have permission to upload!");
		}
	}

	public Doc downloadPublicDocument(String documentID, String downloaderID) throws IOException, IllegalAccessException {
		Optional<Doc> documentToDownload = this.documentRepo.findById(documentID);

		if(documentToDownload.isEmpty()){
			throw new IOException("Document not found!");
		}

		if(!isAccessible(documentToDownload.get().getDepartment(), documentToDownload.get().getAccessLevel(), downloaderID)
				&& !downloaderID.equals(ADMIN_ID)){
			throw new IllegalAccessException("Document not accessible!");
		}

		if(!documentToDownload.get().getDownloaders().contains(downloaderID)) {
			documentToDownload.get().getDownloaders().add(downloaderID);
		}

		return documentToDownload.get();
	}

	//Both Employee and Admin can download a personal document
	public Doc downloadPersonalDocument(int documentID, String downloaderID, String ownerID) throws IllegalArgumentException, IllegalAccessException {
		Optional<Employee> owner = this.employeeRepo.findById(ownerID);

		if(owner.isEmpty()){
			throw new IllegalArgumentException("Employee with id " + ownerID + " doesn't exist");
		}

		List<Doc> personalDocuments = owner.get().getPersonalDocuments();

		if(personalDocuments.size() <= documentID){
			throw new IllegalArgumentException("Document with id " + documentID + " doesn't exists");
		}

		Doc document = personalDocuments.get(documentID);

		if(!downloaderID.equals(ADMIN_ID) && !downloaderID.equals(document.getUploaderID())){
			throw new IllegalAccessException("Document not accessible!");
		}

		return document;
	}

	public void deletePublicDocument(String documentID, String employeeID) throws IllegalArgumentException, IllegalAccessException{
		Optional<Doc> documentToDelete = this.documentRepo.findById(documentID);

		if(documentToDelete.isEmpty()){
			throw new IllegalArgumentException("Document not found!");
		}

		if(employeeID.equals(ADMIN_ID) || documentToDelete.get().getUploaderID().equals(employeeID)){
			this.documentRepo.deleteById(documentID);
		} else {
			throw new IllegalAccessException("You don't have permission to delete document");
		}
	}

	//only admin can delete private documents
	public void deletePersonalDocument(int documentID, String employeeID, String ownerID) throws IllegalArgumentException, IllegalAccessException{
		Optional<Employee> owner = this.employeeRepo.findById(ownerID);

		if(owner.isEmpty()){
			throw new IllegalArgumentException("Owner not found!");
		}

		if(employeeID.equals(ADMIN_ID)){
			List<Doc> personalDocuments = owner.get().getPersonalDocuments();

			if(personalDocuments.size() <= documentID){
				throw new IllegalArgumentException("Document with id " + documentID + " doesn't exists");
			}

			personalDocuments.remove(documentID);

			this.employeeRepo.save(owner.get());
		} else {
			throw new IllegalAccessException("You don't have permission to delete document");
		}
	}


	private boolean isAccessible(String department, int accessLevel, String employeeID) {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if(employee.isEmpty()){
			return false;
		}

		//The Admin always has access
		if(employeeID.equals(ADMIN_ID)){
			return true;
		} else {
			return department.equals(employee.get().getDepartment()) && accessLevel <= employee.get().getAccessLevel();
		}
	}
}
