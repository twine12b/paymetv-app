package com.paymetv.repository;

import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

//@SpringBootApplication(scanBasePackages = "com.paymetv.app")
@ContextConfiguration(classes = AppApplication.class)
@DataJpaTest
@Tag("UserRepositoryTest")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Users user1;
    private Users user2;

    @BeforeEach
    void setup() {
        user1 = new Users();
        user1.setUsername("John");
        user1.setPassword("password");
        user1.setEmail("john.doe@example.com");
        entityManager.persistAndFlush(user1);

        user2 = new Users();
        user2.setUsername("Jane");
        user2.setPassword("password");
        user2.setEmail("jane.doe@example.com");
        entityManager.persistAndFlush(user2);
    }

    @Test
    @DisplayName("findAll() returns every persisted user")
    void testFindAllUsers() {
        List<Users> users = userRepository.findAll();

        assertThat(users)
            .isNotEmpty()
            .hasSize(2);
    }

    @Test
    @DisplayName("findByUsername() returns the correct user")
    void testFindByUsername() {
        Users user = userRepository.findByUsername(user1.getUsername());

        assertNotNull(user.getId());

        assertThat(user)
            .isNotNull()
            .isEqualTo(user1);
    }

    @Test
    @DisplayName("findByEmail() returns the correct user")
    void testFindByEmail() {
        Users user = userRepository.findByEmail(user1.getEmail());

        assertThat(user)
            .isNotNull()
            .isEqualTo(user1);
    }

    @Test
    @DisplayName("save() persists a new user and assigns a generated id")
    void testSaveUser() {
        Users newUser = new Users();
        newUser.setUsername("NewUser");
        newUser.setPassword("password");
        newUser.setEmail("new.user@example.com");

        Users savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getUsername()).isEqualTo("NewUser");
        assertThat(savedUser.getEmail()).isEqualTo("new.user@example.com");
    }

    @Test
    @DisplayName("delete() removes a user")
    void testDeleteUser() {
        userRepository.delete(user2);
        List<Users> users = userRepository.findAll();

        assertThat(users)
            .isNotEmpty()
            .hasSize(1)
            .doesNotContain(user2);
    }

    @Test
    @DisplayName("insert a user")
    void testInsertUser() {
        userRepository.save(user1);
        Users foundUser = userRepository.findByUsername(user1.getUsername());

        assertThat(foundUser)
                .isNotNull()
                .isEqualTo(user1);
    }
}
