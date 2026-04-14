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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

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

        return new InMemoryUserDetailsManager(user, admin);
    }

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
                .logoutSuccessUrl("/?logout=true") // Redirect after logout
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
