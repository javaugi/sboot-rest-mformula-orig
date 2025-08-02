/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgresConnection {
    
    public static void main(String[] args) {
        PostgresConnection main = new PostgresConnection();
        main.testPostgreSQLConn();
    }
        
    private void testPostgreSQLConn() {
        log.info("PostgresConnection running ...");
        String url = "jdbc:postgresql://localhost:5433/algotdb";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "admin");

        try (Connection conn = DriverManager.getConnection(url, props)) {
            if (conn != null) {
                System.out.println("Successfully connected to PostgreSQL!");
            } else {
                System.out.println("Failed to connect to PostgreSQL.");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to PostgreSQL: " + e.getMessage());
        }     
    }
}
