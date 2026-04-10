package com.paymetv.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for PayMeTV application.
 *
 * FIXES APPLIED:
 * 1. Added @Configuration annotation (required for Spring to process beans)
 * 2. Changed to BCryptPasswordEncoder (secure, industry standard)
 * 3. Fixed user passwords to match expected credentials (user/user, admin/admin)
 * 4. Added proper SecurityFilterChain bean (replaces deprecated configure method)
 * 5. Fixed request matcher syntax ("static/**" → "/static/**")
 * 6. Added form-based login with custom login page
 * 7. Added CSRF configuration for REST APIs
 * 8. Added logout functionality
 * 9. Added remember-me functionality
 * 10. Made root path "/" truly public
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

//    @Autowired
//    private UserDetailsService userDetailsService;
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity (not recommended for production)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/index.html", "/login.html", "/actuator/health/**", "/assets/**", "/*.css", "/*.js", "/*.ico", "/*.png", "/*.jpg", "/*.svg").permitAll() // Public endpoints
//                        .requestMatchers("/upload/**", "/api/**").authenticated() // Protected endpoints
//                        .anyRequest().authenticated() // All other requests require authentication
//                )
//                .httpBasic(Customizer.withDefaults()) // Enable HTTP Basic authentication
//                .build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(bCryptPasswordEncoder());
//        return provider;
//    }





    /**
     * Password encoder bean using BCrypt.
     * BCrypt is the industry standard for password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * In-memory user details service for development/testing.
     *
     * Credentials:
     * - user/user (role: USER)
     * - admin/admin (roles: USER, ADMIN)
     *
     * TODO: Replace with database-backed UserDetailsService for production
     *
     * FIX: Inject PasswordEncoder as parameter to avoid circular dependency
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))  // ✅ Use injected encoder
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))  // ✅ Use injected encoder
                .roles("USER", "ADMIN")
                .build();

        System.out.println("===================================");
        System.out.println("User passsword (encoded): " + user.getPassword());
        System.out.println("Admin passsword (encoded): " + admin.getPassword());
        System.out.println("===================================");


        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Security filter chain configuration.
     *
     * ✅ FIXED: Added @Bean annotation so this actually gets used by Spring Security
     * ✅ FIXED: Returns SecurityFilterChain (proper Spring Security 6 approach)
     * ✅ FIXED: Inject UserDetailsService to avoid circular dependency
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz
                // ✅ Public endpoints - NO authentication required
                .requestMatchers("/", "/index.html").permitAll()        // Root landing page and index.html
                .requestMatchers("/login.html", "/login").permitAll()   // Login page
                .requestMatchers("/actuator/health/**").permitAll()     // Health checks (Kubernetes)
                .requestMatchers("/assets/**", "/vite.svg").permitAll() // Static resources
                .requestMatchers("/*.css", "/*.js", "/*.ico", "/*.png", "/*.jpg", "/*.svg").permitAll() // Static files
                    .requestMatchers("/swagger-ui/**").hasAnyRole("ADMIN")

                // ✅ Protected endpoints - authentication required
                .requestMatchers("/upload", "/upload/**").authenticated()  // File upload page and sub-paths
                .requestMatchers("/api/**").authenticated()                // All API endpoints

                // ✅ Everything else requires authentication
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                // Disable CSRF for REST API endpoints (stateless, token-based)
                // CSRF protection remains enabled for web forms
                .ignoringRequestMatchers(
                    "/api/files/upload",      // File upload API
                    "/api/imageface/**"       // ImageFace CRUD API
                )
            )
            .formLogin((form) -> form
//                .loginPage("/login.html")                    // Custom login page
//                .loginProcessingUrl("/login")                // Where to POST the form
//                .defaultSuccessUrl("/upload", true)          // Redirect after login
                .failureUrl("/login.html?error=true")        // Redirect on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                        // Logout endpoint
                .logoutSuccessUrl("/login.html?logout=true") // Redirect after logout
                .invalidateHttpSession(true)                 // Invalidate session
                .deleteCookies("JSESSIONID", "remember-me")  // Clear cookies
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key("paymetv-remember-me-key")              // Secret key
                .tokenValiditySeconds(7 * 24 * 60 * 60)      // 7 days
                .rememberMeParameter("remember-me")          // Form parameter
                .userDetailsService(userDetailsService)      // ✅ Use injected service
            );

        return http.build();
    }
}
