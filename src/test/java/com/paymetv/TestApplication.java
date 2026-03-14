package com.paymetv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.repository.UserRepository;
import com.paymetv.app.service.JsonPayloadCreatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;


/**
 * Minimal Spring Boot configuration anchor for test-slice tests
 * (e.g. {@code @DataJpaTest}) that live under {@code com.paymetv.*} but
 * outside the main application package {@code com.paymetv.app}.
 *
 * <p>Spring Boot's test-context bootstrapper walks upward through the package
 * hierarchy of the test class looking for a {@code @SpringBootConfiguration}.
 * Because {@code AppApplication} is in the sibling branch
 * {@code com.paymetv.app}, it is never found by tests in
 * {@code com.paymetv.repository}.  Placing this class at {@code com.paymetv}
 * (the nearest common ancestor) lets every sub-package test find it.</p>
 *
 * <p>The {@code scanBasePackages} attribute restricts component scanning to the
 * real application package so that only production beans are visible; the
 * {@code @DataJpaTest} slice filter then further limits the context to JPA
 * components only.</p>
 */

//@TestContainers
//@ContextConfiguration(classes = AppApplication.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(classes = AppApplication.class)


//@Testcontainers
//@SpringBootTest(
//        classes = AppApplication.class,
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//)
@SpringBootApplication(scanBasePackages = "com.paymetv.app")
public class TestApplication {
    // No main() needed – this class is only used as a configuration anchor
    // by @DataJpaTest / @WebMvcTest / etc. running under com.paymetv.*
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

//    @Container
//    static MySQLContainer<?> db = new MySQLContainer<>("mysql:8.4")
//            .withUsername("test")
//            .withPassword("test")
//            .withDatabaseName("testdb");
//
//    @DynamicPropertySource
//    static void configure(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", db::getJdbcUrl);
//        registry.add("spring.datasource.username", db::getUsername);
//        registry.add("spring.datasource.password", db::getPassword);
//
//        // Optional but recommended for MySQL
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
//        registry.add("spring.jpa.database-platform",
//                () -> "org.hibernate.dialect.MySQL8Dialect");
//    }


    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        // This test verifies that the Spring application context loads without errors
    }

//    @Test
//    @DisplayName("creates Json from a database object")
//    void testJsonCreationFromDatabaseObject() {
//        Users u = userRepository.findUsersById(1);
//        JsonNode json = jsonPayloadCreatorService.createJsonNode(u);
//        System.out.println(prettyPrintJsonString(json));
//
////        String response = rest.getForObject("/", String.class);
////        System.out.println(response);
//    }

    private String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }
}

