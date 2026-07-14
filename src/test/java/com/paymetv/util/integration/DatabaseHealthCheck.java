package com.paymetv.util.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class DatabaseHealthTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void databaseShouldBeReachable() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            assertTrue(connection.isValid(5),
                    "Database connection is not valid");

            System.out.println("Connected to: "
                    + connection.getMetaData().getDatabaseProductName());

            System.out.println("Version: "
                    + connection.getMetaData().getDatabaseProductVersion());
        }
    }
    @Test
    @DisplayName("Should execute SQL query")
    void shouldExecuteSql() {

        Integer result =
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        Assertions.assertEquals(Optional.of(1), Optional.of(result));
    }

    @Test
    @DisplayName("Should find application database")
    void shouldFindApplicationDatabase() {

        List<String> databases = jdbcTemplate.queryForList(
                "SHOW DATABASES",
                String.class);


        System.out.println("Databases found: " + databases);

        // TODO - make sure that the 'PaymeTv' database has been created.
        assertTrue(databases.contains("INFORMATION_SCHEMA"));
    }
}