package com.paymetv.app;

import com.paymetv.app.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileUploadService fileUploadService;

	@Test
	@DisplayName("Context loads successfully")
	void contextLoads() {
		// This test verifies that the Spring application context loads without errors
	}
}
