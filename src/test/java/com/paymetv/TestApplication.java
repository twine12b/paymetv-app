package com.paymetv;

import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 *
 * <p><strong>IMPORTANT:</strong> This class should NOT contain any @Autowired fields,
 * @Test methods, or any other test logic. It exists solely as a configuration anchor
 * for Spring Boot's test context bootstrapping.</p>
 */
@SpringBootApplication(scanBasePackages = "com.paymetv.app")
public class TestApplication {
    // No main() needed – this class is only used as a configuration anchor
    // by @DataJpaTest / @WebMvcTest / etc. running under com.paymetv.*

    // DO NOT add @Autowired fields here
    // DO NOT add @Test methods here
    // This is a configuration class only
}

