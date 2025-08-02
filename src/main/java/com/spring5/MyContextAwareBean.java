/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class MyContextAwareBean implements ServletContextAware {
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        // Now safe to use servletContext
    }
}
