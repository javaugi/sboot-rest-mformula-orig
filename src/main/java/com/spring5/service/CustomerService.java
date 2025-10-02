/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.entity.Customer;
import com.spring5.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Optional<Customer> findByEmail(String email) {
		return this.customerRepository.findByEmail(email);
	}

	public Customer registerCustomer(String name, String email) {
		return customerRepository.save(Customer.builder().name(name).email(email).build());
	}

}
