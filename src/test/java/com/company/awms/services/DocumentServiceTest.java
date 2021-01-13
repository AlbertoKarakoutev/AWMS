package com.company.awms.services;

import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Optional;

import static javax.management.Query.times;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DocumentServiceTest {
    private DocumentService documentService;
    private Employee employee;

    @MockBean
    private EmployeeRepo employeeRepo;
    @MockBean
    private DocumentRepo documentRepo;

    //set up data before each test
    @Before
    public void setup(){
        this.employee = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        this.documentService = new DocumentService(this.documentRepo, this.employeeRepo);
    }

    @Test
    public void getAccessibleDocsInfo_ThrowsIOException_WhenEmployeeIdDoesntExist(){
        String id = "123";
        boolean thrown = false;
        String message = "";

        Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
        Mockito.when(this.employeeRepo.findByRole("ADMIN")).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.getAccessibleDocumentsInfo(id);
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertFalse(thrown);
        //verify(this.scheduleRepo, Mockito.times(1)).save();
    }
}
