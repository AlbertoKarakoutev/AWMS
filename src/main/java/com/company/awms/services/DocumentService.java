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

	private static final String ADMIN_ID = "5fbe50169283684476c02096";

	private DocumentRepo documentRepo;
	private EmployeeRepo employeeRepo;

	@Autowired
	public DocumentService(DocumentRepo documentRepo, EmployeeRepo employeeRepo) {
		this.documentRepo = documentRepo;
		this.employeeRepo = employeeRepo;
	}

	private Employee getEmployee(String employeeID) throws IOException{
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if(employee.isEmpty()) {
			throw new IOException("Employee with id " + employeeID + " doesn't exist");
		}

		return employee.get();
	}

	//Access to all public documents info form the same dpt. and same or lower level
	public List<DocInfoDTO> getAccessibleDocumentsInfo(String employeeID) throws IOException {
		List<DocInfoDTO> accessibleDocumentsInfo = new ArrayList<>();

		Employee employee = getEmployee(employeeID);

		List<Doc> departmentDocuments = this.documentRepo.findByDepartment(employee.getDepartment());

		for (Doc document : departmentDocuments) {
			if (employee.getLevel() >= document.getLevel()) {
				DocInfoDTO documentInfo = new DocInfoDTO(document.getID(), document.getName(), document.getSize(),
						document.getType(), document.getUploaderID(), employee.getFirstName() + " " + employee.getLastName());
				accessibleDocumentsInfo.add(documentInfo);
			}
		}

		return accessibleDocumentsInfo;
	}

	//Access to all private documents info
	public List<DocInfoDTO> getPersonalDocumentsInfo(String employeeID) throws IOException{
		List<DocInfoDTO> privateDocumentsInfo = new ArrayList<>();
		Employee employee = getEmployee(employeeID);

		ArrayList<Doc> personalDocuments = (ArrayList<Doc>) employee.getPersonalDocuments();

		for (int i = 0; i < personalDocuments.size(); i++) {
			DocInfoDTO documentInfo = new DocInfoDTO(Integer.toString(i), personalDocuments.get(i).getName(),
					personalDocuments.get(i).getSize(), personalDocuments.get(i).getType(), personalDocuments.get(i).getUploaderID(),
					employee.getFirstName() + " " + employee.getLastName());
			privateDocumentsInfo.add(documentInfo);
		}

		return privateDocumentsInfo;
	}

	public void uploadPublicDocument(MultipartFile file, String uploaderID) throws IOException {
		Employee uploader = getEmployee(uploaderID);

		Binary data = new Binary(BsonBinarySubType.BINARY, file.getBytes());
		String fileName = file.getOriginalFilename();
		String fileType = file.getContentType();
		String department = uploader.getDepartment();
		int level = uploader.getLevel();
		LocalDateTime dateTime = LocalDateTime.now();
		long size = file.getSize();

		Doc document = new Doc(data, level, department, fileName, fileType, uploaderID, dateTime, size);

		this.documentRepo.save(document);
	}

	//only the admin can upload the private Docs
	public void uploadPersonalDocument(MultipartFile file, String uploaderID, String ownerID) throws IOException, IllegalAccessException {
		Employee owner = getEmployee(ownerID);

		if(uploaderID.equals(ADMIN_ID)){
			Binary data = new Binary(BsonBinarySubType.BINARY, file.getBytes());
			String fileName = file.getOriginalFilename();
			String fileType = file.getContentType();
			LocalDateTime dateTime = LocalDateTime.now();

			Doc document = new Doc(data, owner.getLevel(), owner.getDepartment(), fileName, fileType, ownerID, dateTime, file.getSize());

			owner.getPersonalDocuments().add(document);
			this.employeeRepo.save(owner);
		} else {
			throw new IllegalAccessException("You don't have permission to upload!");
		}
	}

	public Doc downloadPublicDocument(String documentID, String downloaderID) throws IOException, IllegalAccessException {
		Optional<Doc> documentToDownloadOptional = this.documentRepo.findById(documentID);

		if(documentToDownloadOptional.isEmpty()){
			throw new IOException("Document not found!");
		}

		Doc documentToDownload = documentToDownloadOptional.get();

		if(!isAccessible(documentToDownload.getDepartment(), documentToDownload.getLevel(), downloaderID)
				&& !downloaderID.equals(ADMIN_ID)){
			throw new IllegalAccessException("Document not accessible!");
		}

		documentToDownload.getDownloaderIDs().add(downloaderID);
		this.documentRepo.save(documentToDownload);

		return documentToDownload;
	}

	//Both Employee and Admin can download a personal document
	public Doc downloadPersonalDocument(int documentID, String downloaderID, String ownerID) throws IOException, IllegalAccessException {
		Employee owner = getEmployee(ownerID);

		List<Doc> personalDocuments = owner.getPersonalDocuments();

		if(personalDocuments.size() <= documentID){
			throw new IOException("Document with id " + documentID + " doesn't exists");
		}

		Doc document = personalDocuments.get(documentID);

		if(!downloaderID.equals(ADMIN_ID) && !downloaderID.equals(document.getUploaderID())){
			throw new IllegalAccessException("Document not accessible!");
		}

		return document;
	}

	public void deletePublicDocument(String documentID, String employeeID) throws IOException, IllegalAccessException{
		Optional<Doc> documentToDelete = this.documentRepo.findById(documentID);

		if(documentToDelete.isEmpty()){
			throw new IOException("Document not found!");
		}

		if(employeeID.equals(ADMIN_ID) || documentToDelete.get().getUploaderID().equals(employeeID)){
			this.documentRepo.deleteById(documentID);
		} else {
			throw new IllegalAccessException("You don't have permission to delete document");
		}
	}

	//only admin can delete private documents
	public void deletePersonalDocument(int documentID, String employeeID, String ownerID) throws IOException, IllegalAccessException{
		Employee owner = getEmployee(ownerID);

		if(employeeID.equals(ADMIN_ID)){
			List<Doc> personalDocuments = owner.getPersonalDocuments();

			if(personalDocuments.size() <= documentID){
				throw new IllegalArgumentException("Document with id " + documentID + " doesn't exists");
			}

			personalDocuments.remove(documentID);

			this.employeeRepo.save(owner);
		} else {
			throw new IllegalAccessException("You don't have permission to delete document");
		}
	}

	public List<DocInfoDTO> searchInDocumentByName(List<DocInfoDTO> documents, String name){
		List<DocInfoDTO> foundDocuments = new ArrayList<>();

		Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

		for (DocInfoDTO document : documents) {
			Matcher matcher = pattern.matcher(document.getName());
			boolean matchFound = matcher.find();
			if(matchFound) {
				foundDocuments.add(document);
			}
		}
		return foundDocuments;
	}


	private boolean isAccessible(String department, int level, String employeeID) {
		Optional<Employee> employee = this.employeeRepo.findById(employeeID);

		if(employee.isEmpty()){
			return false;
		}

		//The Admin always has access
		if(employeeID.equals(ADMIN_ID)){
			return true;
		} else {
			return department.equals(employee.get().getDepartment()) && level <= employee.get().getLevel();
		}
	}
}
