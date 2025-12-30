/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.prometheus.jmh;

import com.spring5.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.openjdk.jmh.annotations.*;

/*
✅ Even better for your case (Spring Boot + React + PostgreSQL)
You should use real app benchmarks instead of Shade:

What to measure         Best Tool For You
Web API latency         k6 or Gatling
JVM CPU/memory          VisualVM / JFR
DB time                 pg_stat_statements
JVM GC                  -Xlog:gc*
Real metrics            Prometheus + Grafana (which you already use)
 */
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
@Component
public class JmhCustomerService {

    @Autowired
    private CustomerService customerService;

    //@Benchmark
    public void testPerformance() {
        customerService.findAll();
    }

}
