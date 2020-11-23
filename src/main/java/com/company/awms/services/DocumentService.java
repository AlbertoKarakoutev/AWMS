package com.company.awms.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

	// Access to all documents form the same dpt. and same or lower level
	public List<String> getAccessableDocumentIDs(String accessLevel) {
		List<String> accessibleDocumentIDs = new ArrayList<>();
		int level = 0;
		String department;
		try {
			department = accessLevel.substring(0, 1);
			level = Integer.parseInt(accessLevel.substring(1, accessLevel.length()));
		} catch (Exception e) {
			System.out.println("Error retrieving access level!");
			return null;
		}
		// This sends the same amount of request to the database as the level. If we
		// have level 9 that is too slow
		// TODO: optimize
		for (int i = 0; i <= level; i++) {
			List<Doc> thisLevelDocuments = documentRepo.findByAccessLevel(department + Integer.toString(i));
			for (Doc document : thisLevelDocuments) {
				accessibleDocumentIDs.add(document.getID());
			}
		}
		if (accessibleDocumentIDs.isEmpty()) {
			System.out.println("You do not have access to this module!");
		}
		return accessibleDocumentIDs;
	}

	public void uploadDocument(MultipartFile file, String uploaderID) throws IOException {
		LocalDateTime dateTime = LocalDateTime.now();
		Optional<Employee> uploader = this.employeeRepo.findById(uploaderID);

		if (uploader.isEmpty()) {
			throw new IllegalArgumentException("Employee with id " + uploaderID + " doesn't exist");
		}

		Doc document = new Doc(uploader.get().getAccessLevel());

		document.setUploaderID(uploaderID);
		document.setData(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		document.setUploadDateTime(dateTime);
		double size = file.getSize();
		document.setSize(size);

		documentRepo.save(document);
	}

	public Doc downloadDocument(String documentID, String downloaderID) throws IOException {
		Optional<Doc> documentToDownload = documentRepo.findById(documentID);

		if (documentToDownload.isEmpty()) {
			throw new IOException("Document not found!");
		}

		if (!isAccessible(documentToDownload.get().getAccessLevel(), downloaderID))
			return null;

		if (!documentToDownload.get().getDownloaders().contains(downloaderID)) {
			documentToDownload.get().getDownloaders().add(downloaderID);
		}

		documentRepo.save(documentToDownload.get());
		return documentToDownload.get();
	}

	public void uploadPrivateDocument(MultipartFile file, String uploaderID) throws IOException {
		LocalDateTime dateTime = LocalDateTime.now();
		Optional<Employee> uploader = this.employeeRepo.findById(uploaderID);

		if (uploader.isEmpty()) {
			throw new IllegalArgumentException("Employee with id " + uploaderID + " doesn't exist");
		}

		Doc document = new Doc(uploader.get().getAccessLevel());

		document.setUploaderID(uploaderID);
		document.setData(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		document.setUploadDateTime(dateTime);
		double size = file.getSize();
		document.setSize(size);

		uploader.get().getPrivateDocuments().add(document);
		employeeRepo.save(uploader.get());
	}

	public Doc downloadPrivateDocument(String documentID, String downloaderID, String employeeID) throws IOException {
		Optional<Employee> downloader = employeeRepo.findById(employeeID);
		if (downloader.isEmpty()) {
			throw new IllegalArgumentException("Employee with id " + downloaderID + " doesn't exist");
		}
		Doc document = null;
		for (Doc doc : downloader.get().getPrivateDocuments()) {
			if (doc.getID().equals(documentID)) {
				document = doc;
				break;
			}
		}
		if (downloader.get().getID().equals(downloaderID) || adminCheck(downloaderID)) {
			return document;
		}
		return null;
	}

	private boolean adminCheck(String employeeID) {

		return false;
	}

	private boolean isAccessible(String accessLevel, String employeeID) {
		Employee employee;
		try {
			employee = employeeRepo.findById(employeeID).get();
		} catch (Exception e) {
			System.out.println("Employee not found");
			return false;
		}
		if (!accessLevel.substring(0, 1).equals(employee.getAccessLevel().substring(0, 1))) {
			return false;
		} else {
			int documentAccessLevel = Integer.parseInt(accessLevel.substring(1, accessLevel.length()));
			int employeeAccessLevel = Integer.parseInt(employee.getAccessLevel().substring(1, employee.getAccessLevel().length()));
			if (documentAccessLevel > employeeAccessLevel) {
				return false;
			}
		}
		return true;
	}

	// TODO:
	// The rest of the CRUD methods related to the controller methods
}
