/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.accountservice;

import com.spring5.entity.Account;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    
    @GetMapping
    public List<Account> getAccounts() {
        return List.of(
            new Account(1L, "Savings", new BigDecimal(1000.00)),
            new Account(2L, "Checking", new BigDecimal(500.00))
        );
    }
    
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable long id) {
        return new Account(id, "Savings", new BigDecimal(1000.00));
    }
}

