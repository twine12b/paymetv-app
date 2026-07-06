package com.paymetv.util.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatabaseHealthTest {

    @Autowired
    private DataSource dataSource;

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
}