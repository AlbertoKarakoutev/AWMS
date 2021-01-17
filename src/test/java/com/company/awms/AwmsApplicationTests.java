package com.company.awms;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.awms.controllers.IndexController;
import com.company.awms.data.employees.EmployeeRepo;
import com.company.awms.services.EmployeeService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.company.awms.controllers.AdminController;

@WebMvcTest(IndexController.class)
@RunWith(SpringRunner.class)
class AwmsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeRepo employeeRepo;
	@MockBean
	private EmployeeService employeeService;

	@Before
	public void before(){
	}

	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is3xxRedirection()).andExpect(content().string(containsString("Hello, World")));
	}
}
