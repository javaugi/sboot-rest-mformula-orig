/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

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
@Component
public class BeanLifecycleExample implements InitializingBean, DisposableBean {

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct method called");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("InitializingBean's afterPropertiesSet called");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("PreDestroy method called");
    }

    @Override
    public void destroy() {
        System.out.println("DisposableBean's destroy called");
    }
}
