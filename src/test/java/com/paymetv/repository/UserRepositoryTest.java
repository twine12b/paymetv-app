package com.paymetv.repository;

import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("UserRepositoryTest")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private Users savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(
                Users.builder()
                        .username("John")
                        .password("password")
                        .email("john.doe@example.com")
                        .build()
        );
    }

    @Test
    @DisplayName("context loads the UserRepository bean")
    void testContextLoads() { }

    @Test
    @Order(1)
    @DisplayName("save user and find by username")
    void testSaveAndFindByUsername() {
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("find user by id")
    void testFindById() {
        Users foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @Order(3)
    @DisplayName("find all users")
    void testFindAllUsers() {
        Iterable<Users> users = userRepository.findAll();
        assertThat(users).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("find user by email")
    void testFindByEmail() {
        Users foundUser = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @Order(5)
    @DisplayName("update user")
    void testUpdateUser() {
        Users user = userRepository.findById(savedUser.getId()).orElseThrow();
        user.setUsername("John Updated");
        userRepository.save(user);

        assertThat(userRepository.findById(savedUser.getId()).orElseThrow().getUsername())
                .isEqualTo("John Updated");
    }

    @Test
    @Order(6)
    @DisplayName("delete user")
    void testDeleteUser() {
        userRepository.deleteById(savedUser.getId());
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }
}
