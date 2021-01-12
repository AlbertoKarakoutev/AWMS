package com.company.awms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeDailyReference;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.data.schedule.Day;
import com.company.awms.data.schedule.ScheduleRepo;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduleServiceTest {
	
    private ScheduleService scheduleService;
    private Employee employee;
    private Day day;
    
    @MockBean
    private EmployeeRepo employeeRepo;
    @MockBean
    private ScheduleRepo scheduleRepo;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //set up data before each test
    @Before
    public void setup(){
    	this.day = new Day(LocalDate.parse("2021-01-01"));
    	EmployeeDailyReference mock = new EmployeeDailyReference();
    	EmployeeDailyReference mock2 = new EmployeeDailyReference();
    	mock.setNationalID("1");
    	mock2.setNationalID("2");
    	day.getEmployees().add(mock);
    	day.getEmployees().add(mock2);
//    	//Mockito.when(scheduleRepo.save(scheduleService.any(Day.class)).thenReturn(day);
//    	this.scheduleRepo.save(day);
//    	System.out.println(scheduleRepo.findAll().size());
//    	dayID = this.scheduleRepo.findByDate(LocalDate.parse("2021-01-01")).get().getID();
//    	System.out.println(dayID);
        this.employee = new Employee("1", "Tester", "1234567890", "abcd", "a@a.bg", "4324ffw432", "a0", "a", 0,
                "0889122334", 1234d, new int[]{ 2, 3 }, 3.4);
        this.scheduleService = new ScheduleService(this.scheduleRepo, this.employeeRepo);
    }
    
    @Test
    public void addWorkDayThrowsExceptionWhenNationalIDIsWrong() {
    	String date = "2021-01-01";
    	
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.addWorkDay("wrongID", date, false, null, null);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("Employee not found!", message);
    	
    }

    @Test
    public void addWorkDayThrowsExceptionWhenDateIsWrong() {
    	String id = "1";
    	
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.addWorkDay(id, "wrongDate", false, null, null);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("Text 'wrongDate' could not be parsed at index 0", message);
    	
    }
    
    @Test
    public void addWorkDayThrowsExceptionWhenDayIsNotFound() {
    	String id = "1";
    	
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.addWorkDay(id, "2020-01-01", false, null, null);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("Invalid date", message);
    	
    }
    
    @Test
    public void addWorkDayExecutesFully() {
    	String date = "2021-01-01";
    	String id = "1";
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
		Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	
    	try {
    		scheduleService.addWorkDay(id, date, false, null, null);
       	} catch (IOException e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.scheduleRepo).save(ArgumentMatchers.any(Day.class));
    	
    }

    @Test
    public void deleteWorkDayExecutesFully() {
    	String date = "2021-01-01";
    	String id = "1";
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	
    	try {
    		scheduleService.deleteWorkDay(id, date);
       	} catch (IOException e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.scheduleRepo).save(ArgumentMatchers.any(Day.class));
    	
    }
    
    @Test
    public void declineSwapThrowsIOExceptionWhenIdIsWrong() {
    	String date = "2020-01-01";
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.declineSwap("wrongID", LocalDate.parse(date));
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
    	
    	assertTrue(thrown);
        assertEquals("No such employee!", message);
    }

    @Test
    public void declineSwapExecutesFully() {
    	String date = "2021-01-01";
    	String id = "1";
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	try {
    		scheduleService.declineSwap(id, LocalDate.parse(date));
       	} catch (IOException e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.employeeRepo).save(ArgumentMatchers.any(Employee.class));
    	
    }
    
    @Test
    public void swapEmployeesThrowsExceptionWhenDateIsWrong() {
    	String id = "1";
    	String date = "2021-01-01";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.swapEmployees(id, id, "wrongDate", date);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("Text 'wrongDate' could not be parsed at index 0", message);
    	
    }

    @Test
    public void swapEmployeesThrowsExceptionWhenDayIsNotFound() {
    	String id = "1";
    	String date = "2021-01-01";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.swapEmployees(id, id, "2021-01-02", date);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("Invalid date", message);
    	
    }
    
    @Test
    public void swapEmployeesThrowsNullPointerExceptionWhenNationalIDIsWrong() {
    	String id = "1";
    	String date = "2021-01-01";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
        String message = "";
        
        try {
        	scheduleService.swapEmployees(id, "wrongID", date, date);
        }catch(NullPointerException | IOException e) {
        	thrown = true;
        	message = e.getMessage();
        }
        
        assertTrue(thrown);
        assertEquals("No such EDR in those days", message);
    	
    }

    @Test
    public void swapEmployeesExecutesFully() {

    	String date = "2021-01-01";
    	String id = "1";
    	String id2 = "2";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.employeeRepo.findByNationalID(id2)).thenReturn(Optional.of(this.employee));
    	
    	try {
    		scheduleService.swapEmployees(id, id2, date, date);
       	} catch (IOException e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.scheduleRepo, Mockito.times(2)).save(ArgumentMatchers.any(Day.class));
    	
    }
    
    @Test
    public void swapRequestThrowsIOExceptionWhenReceiverNationalIdIsWrong() {
    	String id = "1";
    	String date = "2020-01-01";
    	
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.swapRequest(id, "wrongID", date, date);
        }catch(IOException e) {
        	thrown = true;
        	message = e.getMessage();
        }
    	
    	assertTrue(thrown);
        assertEquals("Receiver not found!", message);
    }

    @Test
    public void swapRequestThrowsIOExceptionWhenRequesterIdIsWrong() {
    	String id = "1";
    	String date = "2020-01-01";
    	
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.swapRequest("wrongID", id, date, date);
        }catch(IOException e) {
        	thrown = true;
        	message = e.getMessage();
        }
    	
    	assertTrue(thrown);
        assertEquals("Requester not found!", message);
    }

    @Test
    public void swapRequestThrowsIOExceptionWhenDateIsWrong() {
    	String id = "1";
    	String date = "wrongDate";
    	
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.swapRequest(id, id, date, date);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
    	
    	assertTrue(thrown);
        assertEquals("Text 'wrongDate' could not be parsed at index 0", message);
    }

    @Test
    public void swapRequestExecutesFully() {
    	String date = "2021-01-01";
    	String id = "1";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	
    	try {
    		scheduleService.swapRequest(id, id, date, date);
       	} catch (IOException e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.employeeRepo).save(ArgumentMatchers.any(Employee.class));
    	
    }

    @Test
    public void addTaskThrowsIOExceptionWhenDateIsWrong(){
    	String data = "date=wrongDate \nreceiverNationalID=1 \ntitle=title \nbody=body \nreward=1 ";

    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.addTask(data);
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Text 'wrongDate' could not be parsed at index 0", message);
    }

    @Test
    public void addTaskThrowsIOExceptionWhenDayIsNotFound(){
    	String data = "date=2020-01-01 \nreceiverNationalID=1 \ntitle=title \nbody=body \nreward=1 ";

    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.addTask(data);
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Invalid date", message);
    }
    
    @Test
    public void addTaskThrowsExceptionWhenDataIsIncorrect(){

    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.addTask("wrongData");
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Invalid request", message);
    }

    @Test
    public void addTaskExecutesFully(){
    	String date = "2020-01-01";
    	String id = "1";
    	
    	String data = "date="+date+" \nreceiverNationalID="+id+" \ntitle=title \nbody=body \nreward=1 ";

    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	
    	try {
    		this.scheduleService.addTask(data);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	Mockito.verify(this.employeeRepo).save(ArgumentMatchers.any(Employee.class));
    }

    @Test
    public void markTaskAsCompleteThrowsExceptionWhenDateIsWrong() {
    	String id = "1";
    	String taskNumber = "1";
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete(id, taskNumber, "wrongDate");
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Text 'wrongDate' could not be parsed at index 0", message);
    }
    
    @Test
    public void markTaskAsCompleteThrowsExceptionWhenDayIsNotFound() {
    	String id = "1";
    	String taskNumber = "1";
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete(id, taskNumber, "2020-01-01");
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Invalid date", message);
    }
    
    @Test
    public void markTaskAsCompleteThrowsIOExceptionWhenNationalIDIsWrong() {
    	String date = "2020-01-01";
    	String taskNumber = "1";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete("1", taskNumber, date);
    	}catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Employee not found", message);
    }
    
    @Test
    public void markTaskAsCompleteThrowsNumberFormatExceptionWhenTaskNumberIsWrong() {
    	String date = "2020-01-01";
    	String id = "1";
    	
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete(id, "a", date);
    	}catch(NumberFormatException | IOException e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Invalid task number", message);
    }
    
}