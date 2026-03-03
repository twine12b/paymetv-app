package com.paymetv.app.repository;

import com.paymetv.app.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<Users, Integer> {

    // Derived query – Spring Data generates the WHERE clause from the method name
    Users findByEmail(String email);

//    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Users findUserByEmail(String email);

    Users findByUsername(String username);
}
