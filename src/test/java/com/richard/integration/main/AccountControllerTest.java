package com.richard.integration.main;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.richard.controller.ApplicationRunner;
import com.richard.request.TransferRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationRunner.class)
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;
    
//    @Test
//    public void getHello() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/ping").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(equalTo("pong")));
//    }
    
    @Test
    public void pessimiticTransferTest() throws Exception {
    	
    	String originalFrom = "{\"id\": 123456789, \"name\": \"richard\", \"balance\": 10000000.00}";
    	String originalTo = "{\"id\": 987654321, \"name\": \"test\", \"balance\": 80000000.00}";
    	
    	Thread thread1 = new Thread(() -> {
        	TransferRequest request = new TransferRequest();
        	request.setAccountFrom(123456789L);
        	request.setAccountTo(987654321L);
        	request.setAmount(new BigDecimal("500.00"));
        	try {
	        	for(int ctr = 0; ctr < 500; ctr++){
	    	        mvc.perform(MockMvcRequestBuilders.post("/transfer")
	    	           .content(asJsonString(request))
	    	           .contentType(MediaType.APPLICATION_JSON)
	    	           .accept(MediaType.APPLICATION_JSON));
	        	}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
    	});

    	Thread thread2 = new Thread(() -> {
        	TransferRequest request2 = new TransferRequest();
        	request2.setAccountFrom(987654321L);
        	request2.setAccountTo(123456789L);
        	request2.setAmount(new BigDecimal("500.00"));
        	try {
	        	for(int ctr = 0; ctr < 500; ctr++){
	    	        mvc.perform(MockMvcRequestBuilders.post("/transfer")
	    	           .content(asJsonString(request2))
	    	           .contentType(MediaType.APPLICATION_JSON)
	    	           .accept(MediaType.APPLICATION_JSON));
	        	}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
    	});

    	thread1.start();
    	thread2.start();
    	
    	thread1.join();
    	thread2.join();
    	
        mvc.perform(MockMvcRequestBuilders.get("/getAccountById/123456789").accept(MediaType.APPLICATION_JSON))
	       .andExpect(status().isOk())
	       .andExpect(content().json(originalFrom));
        
        mvc.perform(MockMvcRequestBuilders.get("/getAccountById/987654321").accept(MediaType.APPLICATION_JSON))
	       .andExpect(status().isOk())
	       .andExpect(content().json(originalTo));
    	
    }
    
    private String asJsonString(TransferRequest request) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(request);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } 
}
