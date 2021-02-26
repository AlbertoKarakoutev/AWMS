package com.company.awms.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import com.company.awms.modules.base.employees.data.Employee;
import com.company.awms.modules.base.employees.data.EmployeeDailyReference;
import com.company.awms.modules.base.employees.data.EmployeeRepo;
import com.company.awms.modules.base.schedule.ScheduleService;
import com.company.awms.modules.base.schedule.data.Day;
import com.company.awms.modules.base.schedule.data.ScheduleRepo;
import com.company.awms.modules.base.schedule.data.Task;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduleServiceTest {
	
    private ScheduleService scheduleService;
    private Employee employee;
    private Day day;
    private EmployeeDailyReference mock;
    private EmployeeDailyReference mock2;
    
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
    	mock = new EmployeeDailyReference();
    	mock2 = new EmployeeDailyReference();
    	mock.setNationalID("1");
    	mock2.setNationalID("2");
    	mock.setDepartment("a");
    	mock.setLevel(0);
    	mock2.setDepartment("b");
    	mock2.setLevel(1);
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
       	} catch (Exception e) {
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
       	} catch(Exception e) {
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
    public void swapRequestThrowsExceptionWhenReceiverNationalIdIsWrong() {
    	String id = "1";
    	String date = "2020-01-01";
    	
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.swapRequest(id, "wrongID", date, date);
        }catch(Exception e) {
        	thrown = true;
        	message = e.getMessage();
        }
    	
    	assertTrue(thrown);
        assertEquals("Receiver not found!", message);
    }

    @Test
    public void swapRequestThrowsExceptionWhenRequesterIdIsWrong() {
    	String id = "1";
    	String date = "2020-01-01";
    	
    	Mockito.when(this.employeeRepo.findByNationalID(id)).thenReturn(Optional.of(this.employee));
    	
    	boolean thrown = false;
    	String message ="";
    	
    	try {
        	scheduleService.swapRequest("wrongID", id, date, date);
        }catch(Exception e) {
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
       	} catch (Exception e) {
       		e.printStackTrace();
       	}

    	Mockito.verify(this.employeeRepo).save(ArgumentMatchers.any(Employee.class));
    	
    }

    @Test
    public void addTaskThrowsExceptionWhenDateIsWrong(){
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
    public void markTaskAsCompleteThrowsIOExceptionWhenIDIsWrong() {
    	String date = "2020-01-01";
    	String taskNumber = "1";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete("wrongID", taskNumber, date);
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
    
    @Test
    public void markTaskAsCompleteThrowsNullPointerExceptionWhenTaskDoesntExist() {
    	String date = "2020-01-01";
    	String id = "1";
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	boolean thrown = false;
    	String message ="";
    	
    	try {
    		this.scheduleService.markTaskAsComplete(id, "1", date);
    	}catch(NullPointerException | IOException e) {
        	thrown = true;
        	message = e.getMessage();
    	}

    	assertTrue(thrown);
        assertEquals("Task doesn't exist", message);
    }
    
    @Test
    public void markTaskAsCompleteExecutesFully() {
    	String id = "1";
    	String date = "2021-01-01";
    	String taskNum = "0";
    	
    	mock.getTasks().add(new Task());
    	
    	Mockito.when(this.employeeRepo.findById(id)).thenReturn(Optional.of(this.employee));
    	Mockito.when(this.scheduleRepo.findByDate(LocalDate.parse(date))).thenReturn(Optional.of(this.day));
    	Mockito.when(this.employeeRepo.findAllByRole("MANAGER")).thenReturn(List.of(this.employee));
    	try {
    		this.scheduleService.markTaskAsComplete(id, taskNum, date);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	Mockito.verify(this.scheduleRepo).save(ArgumentMatchers.any(Day.class));
    }
    
    @Test
    public void removeReadNotificationsExecutesFully() {
    	List<Employee> allEmployees = new ArrayList<Employee>();
    	allEmployees.add(this.employee);
    	Mockito.when(this.employeeRepo.findAll()).thenReturn(allEmployees);
    	
    	try {
    		this.scheduleService.removeReadNotifications();
    	}catch(Exception  e) {
    		e.printStackTrace();    		
    	}
    	
    	Mockito.verify(this.employeeRepo, Mockito.times(allEmployees.size())).save(ArgumentMatchers.any(Employee.class));
    }
    
    @Test
    public void getDepartmentReturnsNullWhenDepartmentsJSONFileIsWrong() {
    	List<String> JSONData = null;
    	FileWriter mockFR;
    	String existingDepartment = "a";
    	try {
			JSONData = Files.readAllLines(Paths.get("src/main/resources/departments.json"));
			
			mockFR = new FileWriter("src/main/resources/departments.json", true);
			mockFR.write("a");
			mockFR.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	try {
			assertEquals(null, this.scheduleService.getDepartment(existingDepartment));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	
    	try {
			mockFR = new FileWriter("src/main/resources/departments.json", false);
			for(String line : JSONData) {
				mockFR.write(line);
			}
			mockFR.flush();
			mockFR.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Test 
    public void getDepartmentExecutesFully() {
    	String existingDepartment = "a";
    	boolean exception = false;
    	
    	try {
			assertNotEquals(this.scheduleService.getDepartment(existingDepartment), null);
		} catch (Exception e) {
			exception = true;
			e.printStackTrace();
		}
    	assertFalse(exception);
    }
    
    @Test 
    public void getDepartmentAtLevelThrowsExceptionWhenUniversalScheduleIsTrue() {
    	boolean thrown = false;
    	String message = "";
    	try {
    		this.scheduleService.getDepartmentAtLevel("b", 0);
    	}catch(Exception e) {
    		thrown = true;
    		message = e.getMessage();
    	}
    	
    	assertTrue(thrown);
    	assertEquals("This department has a universal schedule", message);
    	
    }
    
    @Test 
    public void getDepartmentAtLevelThrowsExceptionWhenLevelDoesntExist() {
    	boolean thrown = false;
    	String message = "";
    	try {
    		this.scheduleService.getDepartmentAtLevel("a", 3);
    	}catch(Exception e) {
    		thrown = true;
    		message = e.getMessage();
    	}
    	
    	assertTrue(thrown);
    	assertTrue(message.contains("Level doesn't exist") ); 	
    }
    
    @Test 
    public void getDepartmentAtLevelExecutesFully() {
    	String existingDepartment = "a";
    	int existingLevel = 0;
    	boolean exception = false;
    	
    	try {
			assertNotEquals(this.scheduleService.getDepartmentAtLevel(existingDepartment, existingLevel), null);
		} catch (Exception e) {
			exception = true;
			e.printStackTrace();
		}
    	assertFalse(exception);
    }
    
    @Test
    public void isLeaveDayReturnsTrueWhenEmployeeHasALeaveForASpecificDay() {
    	Map<String, Object> leave = new HashMap<String, Object>();
    	leave.put("start", day.getDate().minus(1, ChronoUnit.DAYS));
    	leave.put("end", day.getDate().plus(1, ChronoUnit.DAYS));
    	leave.put("paid", false);
    	employee.getLeaves().add(leave);
    	
    	try {
			assertTrue(this.scheduleService.isLeaveDay(employee, day));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	employee.getLeaves().remove(leave);
    }
    
    @Test
    public void isLeaveDayReturnsFalseWhenEmployeeDoesNotHaveALeaveForASpecificDay() {
    	Map<String, Object> leave = new HashMap<String, Object>();
    	leave.put("start", day.getDate().plus(1, ChronoUnit.DAYS));
    	leave.put("end", day.getDate().plus(2, ChronoUnit.DAYS));
    	leave.put("paid", false);
    	employee.getLeaves().add(leave);
    	
    	try {
			assertFalse(this.scheduleService.isLeaveDay(employee, day));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	employee.getLeaves().remove(leave);
    }
    
    /*Impossible to pass incorrect parameters to apply[schedule-type]Schedule(), 
        because they are called only from applySchedule(), which picks its arguments
        from the departments.json file itself, and can not pass them on incorrectly to the schedule types
    */
    //@Test
    public void applyRegularScheduleExecutesFully() {
    	String existingDepartment = "b";
    	int existingLevel = 1;
    	boolean exception = false;
    	
    	Mockito.when(this.scheduleRepo.findAll()).thenReturn(List.of(this.day));
    	Mockito.when(this.employeeRepo.findByAccessLevel(existingDepartment+ Integer.toString(existingLevel))).thenReturn(List.of(employee));
    	
    	try {
			assertTrue(this.scheduleService.applyRegularSchedule(existingDepartment, existingLevel));
		} catch (Exception e) {
			exception = true;
			e.printStackTrace();
		}
    	assertFalse(exception);
    }

    @Test
    public void viewScheduleReturnsAllEmployeesFromDepartmentLevelWithLowerOrEqualLevel() {
    	//Method should return 1 employee in each day  - the "mock" EmployeeDailyreference
    	Employee viewer = new Employee();
    	viewer.setDepartment("a");
    	viewer.setLevel(2);
    	viewer.setNationalID("3");
    	viewer.setRole("EMPLOYEE");
    	Mockito.when(this.scheduleRepo.findByDate(ArgumentMatchers.any(LocalDate.class))).thenReturn(Optional.of(this.day));
    	List<EmployeeDailyReference>[][] schedule = new ArrayList[2][LocalDate.now().lengthOfMonth() + 1];
    	try {
    		schedule = this.scheduleService.viewSchedule(viewer, YearMonth.now());
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	try {
	    	for(int i = 1; i < schedule.length; i++) {
	    		assertTrue(schedule[0][i].size() == 1);
	    		assertTrue(schedule[0][i].get(0) == mock);
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    @Test
    public void viewScheduleReturnsAllEmployeesWhenViewerRoleIsAdminOrManager() {
    	//Method should return both EDR's in each day
    	Employee viewer = new Employee();
    	viewer.setDepartment("a");
    	viewer.setLevel(2);
    	viewer.setNationalID("3");
    	viewer.setRole("ADMIN");
    	
    	Mockito.when(this.scheduleRepo.findByDate(ArgumentMatchers.any(LocalDate.class))).thenReturn(Optional.of(this.day));
    	List<EmployeeDailyReference>[][] schedule = new ArrayList[2][LocalDate.now().lengthOfMonth() + 1];
    	try {
    		schedule = this.scheduleService.viewSchedule(viewer, YearMonth.now());
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	try {
	    	for(int i = 1; i < schedule.length; i++) {
	    		assertTrue(schedule[0][i].size() == 2);
	    		assertTrue(schedule[0][i].get(0).getNationalID() != viewer.getNationalID());
	    		assertTrue(schedule[0][i].get(1).getNationalID() != viewer.getNationalID());
	    		assertTrue(schedule[0][i].get(0) == mock);
	    		assertTrue(schedule[0][i].get(1) == mock2);
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
}