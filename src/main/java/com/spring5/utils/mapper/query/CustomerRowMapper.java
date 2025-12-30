/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.mapper.query;

import com.spring5.entity.Customer;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author javau
 */
public class CustomerRowMapper {

    public final static RowMapper<Customer> customerRowMapper = (ResultSet rs, int rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        // Add more fields as needed
        return customer;
    };

    public final static RowMapper<Customer> customerLambdaRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        // Add more fields as needed
        return customer;
    };

    public final static RowMapper<Customer> createRowMapper() {
        return (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            return customer;
        };
    }
}
