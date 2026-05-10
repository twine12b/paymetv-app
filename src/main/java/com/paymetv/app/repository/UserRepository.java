package com.paymetv.app.repository;

import com.paymetv.app.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories(basePackages = "com.paymetv.app.repository")
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String mail);

}
