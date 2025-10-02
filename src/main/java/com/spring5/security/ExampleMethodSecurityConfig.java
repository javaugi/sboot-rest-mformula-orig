/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @author javaugi
 */
// @Configuration
@EnableMethodSecurity(prePostEnabled = true, // Enables @PreAuthorize and @PostAuthorize
		securedEnabled = true, // Enables @Secured
		jsr250Enabled = true // Enables @RolesAllowed
)
public class ExampleMethodSecurityConfig {

}
