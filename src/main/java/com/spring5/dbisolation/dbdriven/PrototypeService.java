/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/*
2. Explain Spring Bean lifecycle and different scopes - Detailed Description:
The Spring Bean lifecycle consists of several phases: instantiation, population of properties,
    initialization, and destruction.
Spring provides several scopes for beans:
    Singleton (default) - One instance per Spring container
    Prototype - New instance each time requested
    Request - One instance per HTTP request
    Session - One instance per HTTP session
    Application - One instance per ServletContext
 */
@Service
@Scope("prototype")
public class PrototypeService {
}
