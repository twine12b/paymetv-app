# Authentication System Setup Guide

## Overview

The PayMeTV application now has a complete authentication system with:
- ✅ Custom login page at `/login.html`
- ✅ Form-based authentication with Spring Security
- ✅ BCrypt password encryption
- ✅ Remember-me functionality (7 days)
- ✅ Protected `/upload` endpoint
- ✅ In-memory users for development
- ✅ Logout functionality

---

## Default Users (Development)

| Username | Password | Roles | Access Level |
|----------|----------|-------|--------------|
| `user` | `user` | USER | Standard user access |
| `admin` | `admin` | USER, ADMIN | Full administrative access |

**⚠️ Important:** These are for development/testing only. Replace with database-backed users for production.

---

## Usage

### 1. Access Protected Pages

```
1. Navigate to http://localhost/upload
2. You'll be redirected to http://localhost/login.html
3. Enter username: user, password: user
4. After login, you're redirected to /upload
```

### 2. Logout

```
Navigate to http://localhost/logout
You'll be logged out and redirected to /login.html?logout=true
```

### 3. Remember Me

```
Check the "Remember me for 7 days" checkbox when logging in
Your session will persist for 7 days even after closing the browser
```

---

## Production Setup: Database-Backed Users

### Step 1: Create User Entity

```java
package com.paymetv.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;  // BCrypt encrypted
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(nullable = false)
    private String roles;  // Comma-separated: "USER,ADMIN"
}
```

### Step 2: Create User Repository

```java
package com.paymetv.app.repository;

import com.paymetv.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

### Step 3: Create Custom UserDetailsService

```java
package com.paymetv.app.service;

import com.paymetv.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Arrays.stream(user.getRoles().split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()))
                .disabled(!user.isEnabled())
                .build();
    }
}
```

### Step 4: Update SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;  // Inject custom service
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Remove the in-memory userDetailsService() bean
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ... existing configuration ...
            .rememberMe(rememberMe -> rememberMe
                .key("paymetv-remember-me-key")
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .rememberMeParameter("remember-me")
                .userDetailsService(customUserDetailsService)  // Use custom service
            );
        
        return http.build();
    }
}
```

### Step 5: Create Initial Users in Database

```java
package com.paymetv.app.config;

import com.paymetv.app.domain.User;
import com.paymetv.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            // Only create if users don't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("your-secure-password"))
                        .email("admin@paymetv.com")
                        .enabled(true)
                        .roles("USER,ADMIN")
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
```

---

## Security Endpoints

| Endpoint | Access | Purpose |
|----------|--------|---------|
| `/` | Public | Landing page |
| `/login.html` | Public | Login page |
| `/login` | Public | Login form submission |
| `/logout` | Authenticated | Logout |
| `/upload` | **Authenticated** | File upload page (protected) |
| `/actuator/health/**` | Public | Kubernetes health checks |
| `/api/files/upload` | Authenticated | File upload API |
| `/api/imageface/**` | Authenticated | ImageFace CRUD API |

---

## Testing

Tests use `@WithMockUser` to bypass authentication:

```java
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser  // Provides mock authenticated user
public class MyControllerTest {
    // Tests run with authenticated context
}
```

---

## Customization

### Change Remember-Me Duration

```java
.rememberMe(rememberMe -> rememberMe
    .tokenValiditySeconds(30 * 24 * 60 * 60)  // 30 days instead of 7
)
```

### Change Default Redirect

```java
.formLogin((form) -> form
    .defaultSuccessUrl("/dashboard", true)  // Redirect to /dashboard instead of /upload
)
```

### Add Password Requirements

Create a custom validator:

```java
public class PasswordValidator {
    public static boolean isValid(String password) {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }
}
```

---

## Security Best Practices

✅ **Implemented:**
- BCrypt password encryption
- CSRF protection for forms
- Secure session management
- Remember-me tokens
- Logout invalidates session

⚠️ **TODO for Production:**
- Enable HTTPS/TLS
- Implement password complexity rules
- Add account lockout after failed attempts
- Enable security headers
- Add audit logging
- Implement password reset flow
- Add email verification
- Use strong remember-me key (externalize to config)

---

## Troubleshooting

### Issue: Redirected to login in infinite loop
**Solution:** Ensure login page and resources are permitted:
```java
.requestMatchers("/login.html", "/login", "/assets/**").permitAll()
```

### Issue: CSRF token errors
**Solution:** CSRF is enabled for forms, disabled for REST APIs. Check configuration.

### Issue: Remember-me doesn't work
**Solution:** Ensure:
1. Checkbox has name="remember-me"
2. UserDetailsService is properly configured
3. Remember-me key is set

---

## Files Modified

1. ✅ `SecurityConfig.java` - Complete authentication configuration
2. ✅ `login.html` - Custom login page with styling
3. ✅ `pom.xml` - Added spring-security-test dependency
4. ✅ Test classes - Added @WithMockUser annotation

**Total Changes:** 4 files
