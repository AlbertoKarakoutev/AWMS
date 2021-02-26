package com.company.awms.cntrollers;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.awms.modules.base.IndexController;
import com.company.awms.modules.base.employees.EmployeeService;
import com.company.awms.modules.base.employees.data.EmployeeRepo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
@RunWith(SpringRunner.class)
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepo employeeRepo;
    @MockBean
    private EmployeeService employeeService;

    @Before
    public void before(){
    }

//    @Test
//    public void shouldReturnDefaultMessage() throws Exception {
//        this.mockMvc.perform(get("https://localhost/")).andDo(print()).andExpect(status().is3xxRedirection()).andExpect(content().string(containsString("Hello, World")));
//    }
}
