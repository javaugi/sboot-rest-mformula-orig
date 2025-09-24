/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/*
5) Enable automatic ETag for GET/HEAD responses (ShallowEtagHeaderFilter)
Spring provides a filter that calculates ETag by buffering response. Useful for small responses.
 */
@Configuration
public class FiltersConfig {
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> fr = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        fr.addUrlPatterns("/api/*"); // limit to API paths that are safe to buffer
        fr.setName("etagFilter");
        return fr;
    }
}
