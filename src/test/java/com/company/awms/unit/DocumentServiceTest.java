package com.company.awms.unit;

import com.company.awms.data.documents.DocumentRepo;
import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.services.DocumentService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DocumentServiceTest {
    private DocumentService documentService;
    private Employee employee;

    @Mock
    private EmployeeRepo employeeRepo;
    @Mock
    private DocumentRepo documentRepo;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //set up data before each test
    @Before
    public void setup(){
        this.employee = new Employee("Test", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a3", "a", 3,
                "0889122334", 1234d, new int[]{ 2, 3 });
        this.documentService = new DocumentService(this.documentRepo, this.employeeRepo);
    }

    @Test
    public void getAccessibleDocsInfoThrowsIOExceptionWhenEmployeeIdDoesntExist(){
        String id = "123";
        boolean thrown = false;
        String message = "";

        //Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));

        try {
            this.documentService.getAccessibleDocumentsInfo("1234");
        } catch (IOException e) {
            thrown = true;
            message = e.getMessage();
        }

        assertTrue(thrown);
        assertEquals("Employee with id 1234 doesn't exist", message);
    }
}
