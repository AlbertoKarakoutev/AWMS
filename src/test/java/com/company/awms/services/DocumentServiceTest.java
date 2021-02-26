package com.company.awms.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.company.awms.modules.base.documents.DocumentService;
import com.company.awms.modules.base.documents.data.Doc;
import com.company.awms.modules.base.documents.data.DocInfoDTO;
import com.company.awms.modules.base.documents.data.DocumentRepo;
import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DocumentServiceTest {
    private DocumentService documentService;
    private Employee employee;
    private Employee admin;
    private List<Doc> docList;

    @MockBean
    private EmployeeRepo employeeRepo;
    @MockBean
    private DocumentRepo documentRepo;

    //set up data before each test
    @Before
    public void setup(){
        this.employee = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        this.admin = new Employee("Admin", "Bai", "Admin", "adminpass", "admin@a.bg", "dafw43245dwwt", "a3", "a", 3,
                "0889122343", 12354d, new int[]{ 2, 3 }, 432);

        this.documentService = new DocumentService(this.documentRepo, this.employeeRepo);
    }

    @Test
    public void getAccessibleDocumentsInfoThrowsIOExceptionWhenEmployeeIdDoesntExist(){
        String id = "1234";
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById("123")).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.getAccessibleDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + id + " doesn't exist", message);
    }

    @Test
    public void getAccessibleDocumentsInfoThrowsIOExceptionWhenAdminDoesntExist(){
        String id = "123";
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.empty());

        try {
            this.documentService.getAccessibleDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Admin not found!", message);
    }

    @Test
    public void getAccessibleDocumentsInfoReturnsAllDocumentsWhenEmployeeIsAdmin(){
        this.docList = new ArrayList<>();
        this.docList.add(new Doc(1));
        this.docList.add(new Doc(2));
        this.docList.add(new Doc(3));
        String id = "123";
        boolean thrown = false;

        this.admin.setId(id);
        this.employee.setId(id);

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));
        Mockito.when(this.documentRepo.findAll()).thenReturn(docList);

        List<DocInfoDTO> docInfoList = new ArrayList<>();
        try {
            docInfoList = this.documentService.getAccessibleDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
        }

        assertFalse(thrown);
        assertEquals(3, docInfoList.size());
    }

    @Test
    public void getAccessibleDocumentsInfoReturns1DocumentWhenEmployeeIsNotAdmin(){
        this.docList = new ArrayList<>();
        this.docList.add(new Doc(1));
        String id = "123";
        boolean thrown = false;

        this.admin.setId("1234");
        this.employee.setId(id);

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));
        Mockito.when(this.documentRepo.findByDepartment(this.employee.getDepartment())).thenReturn(docList);

        List<DocInfoDTO> docInfoList = new ArrayList<>();
        try {
            docInfoList = this.documentService.getAccessibleDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
        }

        assertFalse(thrown);
        assertEquals(1, docInfoList.size());
    }

    @Test
    public void getPersonalDocumentsInfoThrowsIOExceptionWhenEmployeeIdDoesntExist(){
        String id = "1234";
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById("123")).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.getPersonalDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + id + " doesn't exist", message);
    }

    @Test
    public void getPersonalDocumentsInfoReturnsAllDocumentsWhenEmployeeExists(){
        this.docList = new ArrayList<>();
        this.docList.add(new Doc(1));
        this.docList.add(new Doc(2));
        this.docList.add(new Doc(3));
        this.employee.setPersonalDocuments(this.docList);

        String id = "123";
        boolean thrown = false;

        this.admin.setId(id);
        this.employee.setId(id);

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));

        List<DocInfoDTO> docInfoList = new ArrayList<>();
        try {
            docInfoList = this.documentService.getPersonalDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
        }

        assertFalse(thrown);
        assertEquals(3, docInfoList.size());
    }

    @Test
    public void uploadPublicDocumentThrowsIOExceptionWhenEmployeeIdDoesntExist(){
        String id = "1234";
        boolean thrown = false;
        String message = "";
        String name = "departments.json";
        String originalFileName = "departments.json";
        String contentType = "text/plain";
        byte[] content = null;
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

        Mockito.when(this.employeeRepo.findById("123")).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.uploadPublicDocument(result, id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + id + " doesn't exist", message);
    }

    @Test
    public void uploadPublicDocumentSavesDocument(){
        String id = "123";
        boolean thrown = false;
        Path path = Paths.get("src/main/resources/departments.json");
        String name = "departments.json";
        String originalFileName = "departments.json";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.documentRepo.save(ArgumentMatchers.any(Doc.class))).thenReturn(null);

        try {
            this.documentService.uploadPublicDocument(result, id);
        } catch (IOException e) {
            thrown = true;
        }

        assertFalse(thrown);
        verify(this.documentRepo).save(ArgumentMatchers.any(Doc.class));
    }

    @Test
    public void uploadPersonalDocumentThrowsIOExceptionWhenEmployeeIdDoesntExist(){
        String id = "1234";
        boolean thrown = false;
        String message = "";
        Path path = Paths.get("src/main/resources/departments.json");
        String name = "departments.json";
        String originalFileName = "departments.json";
        String contentType = "text/plain";
        byte[] content = null;
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

        Mockito.when(this.employeeRepo.findById("123")).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.uploadPersonalDocument(result, id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + id + " doesn't exist", message);
    }

    @Test
    public void uploadPersonalDocumentSavesDocument(){
        String id = "123";
        boolean thrown = false;
        Path path = Paths.get("src/main/resources/departments.json");
        String name = "departments.json";
        String originalFileName = "departments.json";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);

        this.employee.setId(id);

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.save(this.employee)).thenReturn(this.employee);

        try {
            this.documentService.uploadPersonalDocument(result, id);
        } catch (IOException e) {
            thrown = true;
        }

        assertFalse(thrown);
        verify(this.employeeRepo).save(this.employee);
    }

    @Test
    public void downloadPublicDocumentThrowsIOExceptionWhenDocumentIdDoesntExist(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        String message = "";
        Doc doc = new Doc(1);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));

        try {
            this.documentService.downloadPublicDocument("213", downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not found!", message);
    }

    @Test
    public void downloadPublicDocumentThrowsIllegalAccessExceptionWhenDownloaderIdDoesntExist(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        String message = "";
        Doc doc = new Doc(1);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.downloadPublicDocument(documentId, "243");
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not accessible!", message);
    }

    @Test
    public void downloadPublicDocumentThrowsIOExceptionWhenAdminDoesntExist(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        String message = "";
        Doc doc = new Doc(1);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.empty());

        try {
            this.documentService.downloadPublicDocument(documentId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Admin not found!", message);
    }

    @Test
    public void downloadPublicDocumentThrowsIllegalAccessExceptionWhenDocumentLevelIsHigherThenEmployeeLevel(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        String message = "";
        Doc doc = new Doc(null, 3, "a", "docName", "docType", "123", LocalDateTime.now(), 123L);
        this.employee.setLevel(1);
        this.admin.setId("123123");

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        try {
            this.documentService.downloadPublicDocument(documentId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not accessible!", message);
    }

    @Test
    public void downloadPublicDocumentThrowsIllegalAccessExceptionWhenDocumentDepartmentIsDifferentFromEmployeeDepartment(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        String message = "";
        Doc doc = new Doc(null, 1, "a", "docName", "docType", "123", LocalDateTime.now(), 123L);
        this.employee.setLevel(1);
        this.employee.setDepartment("b");
        this.admin.setId("123123");

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        try {
            this.documentService.downloadPublicDocument(documentId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not accessible!", message);
    }

    @Test
    public void downloadPublicDocumentDownloadsDocumentWhenEmployeeIsAdmin(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        Doc doc = new Doc(1);
        this.admin.setId(downloaderId);
        this.employee.setId(downloaderId);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));
        Mockito.when(this.documentRepo.save(ArgumentMatchers.any(Doc.class))).thenReturn(doc);

        try {
            this.documentService.downloadPublicDocument(documentId, downloaderId);
        } catch (Exception e) {
            thrown = true;
        }

        assertFalse(thrown);
        verify(this.documentRepo).save(ArgumentMatchers.any(Doc.class));
    }

    @Test
    public void downloadPublicDocumentDownloadsDocumentWhenEmployeeHasAccess(){
        String downloaderId = "123";
        String documentId = "f1";
        boolean thrown = false;
        Doc doc = new Doc(null, 1, "a", "docName", "docType", "123", LocalDateTime.now(), 123L);
        this.employee.setLevel(1);
        this.employee.setDepartment("a");
        this.admin.setId("123123");

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(doc));
        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));
        Mockito.when(this.documentRepo.save(ArgumentMatchers.any(Doc.class))).thenReturn(doc);

        try {
            this.documentService.downloadPublicDocument(documentId, downloaderId);
        } catch (Exception e) {
            thrown = true;
        }

        assertFalse(thrown);
        verify(this.documentRepo).save(ArgumentMatchers.any(Doc.class));
    }

    @Test
    public void downloadPersonalDocumentThrowsIOExceptionExceptionWhenOwnerIdDoesntExist(){
        String downloaderId = "123";
        String ownerId = "123";
        int documentId = 0;
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.downloadPersonalDocument(documentId, "243", downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + 243 + " doesn't exist", message);
    }

    @Test
    public void downloadPersonalDocumentThrowsIOExceptionExceptionWhenAdminDoesntExist(){
        String downloaderId = "123";
        String ownerId = "123";
        int documentId = 0;
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.empty());

        try {
            this.documentService.downloadPersonalDocument(documentId, ownerId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Admin not found!", message);
    }

    @Test
    public void downloadPersonalDocumentThrowsIllegalAccessExceptionWhenDownloaderIsNotAdminOrOwner(){
        String downloaderId = "12345";
        String ownerId = "123";
        int documentId = 0;
        boolean thrown = false;
        String message = "";
        this.admin.setId("1234");
        this.employee.setId(downloaderId);
        Employee owner = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        owner.setId(ownerId);

        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        try {
            this.documentService.downloadPersonalDocument(documentId, ownerId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not accessible!", message);
    }

    @Test
    public void downloadPersonalDocumentThrowsIOExceptionWhenDocumentIdIsMoreThenOrEqualsToPersonalDocumentsSize(){
        String downloaderId = "123";
        String ownerId = "123";
        int documentId = 0;
        boolean thrown = false;
        String message = "";
        this.admin.setId("1234");
        this.employee.setId(downloaderId);
        Employee owner = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        owner.setId(ownerId);

        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        try {
            this.documentService.downloadPersonalDocument(documentId, ownerId, downloaderId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document with id " + documentId + " doesn't exists", message);
    }

    @Test
    public void downloadPersonalDocumentReturnsDocumentWhenFound(){
        String downloaderId = "123";
        String ownerId = "123";
        int documentId = 1;
        boolean thrown = false;
        this.admin.setId("1234");
        this.employee.setId(downloaderId);
        Employee owner = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);

        List<Doc> personalDocuments = new ArrayList<>();
        personalDocuments.add(new Doc(null, 1, "a", "docName1", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName2", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName3", "docType", "123", LocalDateTime.now(), 123L));
        owner.setPersonalDocuments(personalDocuments);
        owner.setId(ownerId);

        Mockito.when(this.employeeRepo.findById(downloaderId)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        Doc result = null;
        try {
            result = this.documentService.downloadPersonalDocument(documentId, ownerId, downloaderId);
        } catch (Exception e) {
            thrown = true;
        }

        assertFalse(thrown);
        assertNotNull(result);
        assertEquals("docName2", result.getName());
    }

    @Test
    public void deletePublicDocumentThrowsIOExceptionWhenDocumentIdDoesntExist(){
        String documentId = "123";
        String employeeId = "123";
        boolean thrown = false;
        String message = "";

        Doc docToDelete = new Doc(null, 1, "a", "docName1", "docType", "123", LocalDateTime.now(), 123L);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(docToDelete));

        try {
            this.documentService.deletePublicDocument("1234", employeeId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document not found!", message);
    }

    @Test
    public void deletePublicDocumentThrowsIOExceptionWhenAdminDoesntExist(){
        String documentId = "d123";
        String employeeId = "123";
        boolean thrown = false;
        String message = "";

        Doc docToDelete = new Doc(null, 1, "a", "docName1", "docType", "123", LocalDateTime.now(), 123L);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(docToDelete));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.empty());

        try {
            this.documentService.deletePublicDocument(documentId, employeeId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Admin not found!", message);
    }

    @Test
    public void deletePublicDocumentThrowsIllegalAccessExceptionWhenEmployeeIsNotAdminAndIsNotUploader(){
        String documentId = "d123";
        String employeeId = "123";
        boolean thrown = false;
        String message = "";
        this.admin.setId("adminId");
        Doc docToDelete = new Doc(null, 1, "a", "docName1", "docType", "1234", LocalDateTime.now(), 123L);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(docToDelete));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));

        try {
            this.documentService.deletePublicDocument(documentId, employeeId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("You don't have permission to delete document", message);
    }

    @Test
    public void deletePublicDocumentDeletesDocumentWhenEmployeeIsAdmin(){
        String documentId = "d123";
        String employeeId = "123";
        boolean thrown = false;
        this.admin.setId("123");
        Doc docToDelete = new Doc(null, 1, "a", "docName1", "docType", "1234", LocalDateTime.now(), 123L);

        Mockito.when(this.documentRepo.findById(documentId)).thenReturn(Optional.of(docToDelete));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.admin));
        Mockito.doNothing().when(this.documentRepo).deleteById(documentId);

        try {
            this.documentService.deletePublicDocument(documentId, employeeId);
        } catch (Exception e) {
            thrown = true;
        }

        assertFalse(thrown);
        verify(this.documentRepo).deleteById(documentId);
    }

    @Test
    public void deletePersonalDocumentThrowsIOExceptionExceptionWhenOwnerIdDoesntExist(){
        String ownerId = "123";
        int documentId = 0;
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.deletePersonalDocument(documentId, "3");
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id " + 3 + " doesn't exist", message);
    }

    @Test
    public void deletePersonalDocumentThrowsIOExceptionWhenDocumentIdIsMoreThenOrEqualsToPersonalDocumentsSize(){
        String ownerId = "123";
        int documentId = 4;
        boolean thrown = false;
        String message = "";
        Employee owner = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        owner.setId(ownerId);
        List<Doc> personalDocuments = new ArrayList<>();
        personalDocuments.add(new Doc(null, 1, "a", "docName1", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName2", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName3", "docType", "123", LocalDateTime.now(), 123L));
        owner.setPersonalDocuments(personalDocuments);
        owner.setId(ownerId);

        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(owner));

        try {
            this.documentService.deletePersonalDocument(documentId, ownerId);
        } catch (Exception e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Document with id " + documentId + " doesn't exists", message);
    }

    @Test
    public void deletePersonalDocumentDeletesDocumentWhenDocumentIsFound(){
        String ownerId = "123";
        int documentId = 2;
        boolean thrown = false;
        Employee owner = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        owner.setId(ownerId);
        List<Doc> personalDocuments = new ArrayList<>();
        personalDocuments.add(new Doc(null, 1, "a", "docName1", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName2", "docType", "123", LocalDateTime.now(), 123L));
        personalDocuments.add(new Doc(null, 1, "a", "docName3", "docType", "123", LocalDateTime.now(), 123L));
        owner.setPersonalDocuments(personalDocuments);
        owner.setId(ownerId);

        Mockito.when(this.employeeRepo.findById(ownerId)).thenReturn(Optional.of(owner));

        try {
            this.documentService.deletePersonalDocument(documentId, ownerId);
        } catch (Exception e) {
            thrown = true;
        }

        assertFalse(thrown);
        assertEquals(2, owner.getPersonalDocuments().size());
    }

    @Test
    public void searchInDocumentByNameFindsNothingWhenNoNameMatchesPattern(){
        String nameToSearch = "123";
        List<DocInfoDTO> documents = new ArrayList<>();
        documents.add(new DocInfoDTO("0", "name0", 123d, "txt", "123", "owner1"));
        documents.add(new DocInfoDTO("1", "name1", 123d, "txt", "123", "owner1"));
        documents.add(new DocInfoDTO("2", "name2", 123d, "txt", "123", "owner1"));

        List<DocInfoDTO> result = this.documentService.searchInDocumentByName(documents, nameToSearch);

        assertEquals(0, result.size());
    }

    @Test
    public void searchInDocumentByNameFindsDocsWhenNamesMatchPattern(){
        String nameToSearch = "name";
        List<DocInfoDTO> documents = new ArrayList<>();
        documents.add(new DocInfoDTO("0", "name0", 123d, "txt", "123", "owner1"));
        documents.add(new DocInfoDTO("1", "name1", 123d, "txt", "123", "owner1"));
        documents.add(new DocInfoDTO("2", "name2", 123d, "txt", "123", "owner1"));

        List<DocInfoDTO> result = this.documentService.searchInDocumentByName(documents, nameToSearch);

        assertEquals(3, result.size());
    }
}
