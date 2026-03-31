package com.paymetv.repository;

import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("UserRepositoryTest")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("context loads the UserRepository bean")
    void testContextLoads() { }

    @Test
    @Order(1)
    @Rollback(false)
    @DisplayName("save user and find by username")
    void testSaveAndFindByUsername() {
        Users user = Users.builder()
                .username("John")
                .password("password")
                .email("john.doe@example.com")
                .build();

        userRepository.save(user);
        assertThat(user.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("find user by id")
    void testFindById() {
        Users foundUser = userRepository.findById(1L).get();
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
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
        Users foundUser = userRepository.findByEmail("john.doe@example.com").get();
        assertThat(foundUser).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("update user")
    void testUpdateUser() {
        Users user = userRepository.findById(1L).get();
        user.setUsername("John Updated");
        userRepository.save(user);

        assertThat(userRepository.findById(1L).get().getUsername()).isEqualTo("John Updated");
    }

    @Test
    @Order(6)
    @Rollback(false)
    @DisplayName("delete user")
    void testDeleteUser() {
        userRepository.deleteById(1L);
        assertThat(userRepository.findById(1L)).isEmpty();
    }
}
