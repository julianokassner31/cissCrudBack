package br.com.jkassner.ciss_crud_back.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jasypt.encryption.StringEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jkassner.ciss_crud_back.dto.EmployerDto;
import br.com.jkassner.ciss_crud_back.model.Employer;
import br.com.jkassner.ciss_crud_back.repository.EmployerRepository;
import ma.glasnost.orika.MapperFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployerControllerTest {

	@Autowired
    private EmployerController controller;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	MapperFactory mapperFactory;
	
	@Autowired
	EmployerRepository repository;
	
	@Autowired
	StringEncryptor stringEncryptor;
	
	Employer employer;
	
	@Before
	public void createEmployer() {
		employer = mockEmployer("mock", "teste", "mock@gmail.com", 11111111110l);
    	repository.save(employer);
	}
	
	@After
	public void deleteEmployer() {
		repository.delete(employer);
	}
	
	@Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldReturnEmployers() throws Exception {
        this.mockMvc.perform(get("/employers")).andDo(print()).andExpect(status().isOk())
        	.andExpect(content().string(containsString("employers")));
    }
    
    @Test
    public void shouldReturnEmployersAfterSaveEmployer() throws Exception {
        this.mockMvc.perform(post("/employers")
        		.content(requestBody(mockEmployerDto("user", "saved", "saved@email.com", 41235689221l)))
        		.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("employers")));
    }
    
    @Test
    public void shouldReturnEmployersAfterDeleteEmployer() throws Exception {
        this.mockMvc.perform(delete("/employers/" + stringEncryptor.encrypt("1")))
        	.andDo(print()).andExpect(status().isOk())
        	.andExpect(content().string(containsString("employers")));
    }
    
    @Test
    public void shouldReturnUpdatedEmployerAfterUpdateEmployer() throws Exception {
    	String encrypt = stringEncryptor.encrypt(String.valueOf(employer.getId()));
    	EmployerDto employerDto = new EmployerDto();
    	employerDto.setId(encrypt);
    	employerDto.setEmail(employer.getEmail());
    	employerDto.setFirstname(employer.getFirstname());
    	employerDto.setLastname("Updated");// somente alterei o sobrenome
    	employerDto.setPis(employer.getPis());
    	this.mockMvc.perform(put("/employers")
        	.content(requestBody(employerDto))
        	.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        	.andDo(print()).andExpect(status().isOk())
        	.andExpect(content().string(containsString("\"firstname\":\"mock\",\"lastname\":\"Updated\",\"email\":\"mock@gmail.com\",\"pis\":11111111110")));
    }
    
    @Test
    public void shouldReturnEmployerMatcherQueryFirstName() throws Exception {
    	this.mockMvc.perform(get("/employers/find")
    			.param("query", "mock"))
    	.andDo(print()).andExpect(status().isOk())
    	.andExpect(content().string(containsString("\"firstname\":\"mock\"")));
    }
    
    @Test
    public void shouldReturnEmployerMatcherQueryLastName() throws Exception {
    	this.mockMvc.perform(get("/employers/find")
    			.param("query", "teste"))
    	.andDo(print()).andExpect(status().isOk())
    	.andExpect(content().string(containsString("\"lastname\":\"teste\"")));
    }
    
    @Test
    public void shouldReturnEmployerMatcherQueryEmail() throws Exception {
    	this.mockMvc.perform(get("/employers/find")
    			.param("query", "mock@gmail.com"))
    	.andDo(print()).andExpect(status().isOk())
    	.andExpect(content().string(containsString("\"email\":\"mock@gmail.com\"")));
    }
    
    @Test
    public void shouldReturnEmployerMatcherQueryPis() throws Exception {
    	this.mockMvc.perform(get("/employers/find")
    			.param("query", "11111111110"))
    	.andDo(print()).andExpect(status().isOk())
    	.andExpect(content().string(containsString("\"pis\":11111111110")));
    }
    
    private EmployerDto mockEmployerDto(String firstname, String lastname, String email, long pis) {
    	EmployerDto employerDto = new EmployerDto();
    	employerDto.setFirstname(firstname);
    	employerDto.setLastname(lastname);
    	employerDto.setEmail(email);
    	employerDto.setPis(pis);
    	return employerDto;
    }
    
    private Employer mockEmployer(String firstname, String lastname, String email, long pis) {
    	Employer employer = new Employer();
    	employer.setFirstname(firstname);
    	employer.setLastname(lastname);
    	employer.setEmail(email);
    	employer.setPis(pis);
    	return employer;
    }
    
    public static String requestBody(Object request) {
        try {
          return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
}
