/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.spring5.entity.Customer;
import io.micrometer.core.instrument.MeterRegistry;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

//This creates a metric like system_cpu_usage you can view at:
//http://localhost:8080/actuator/prometheus
@Component
public class CpuMonitoring {

    private final MeterRegistry registry;
    private final OperatingSystemMXBean osBean;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> rowMapper;
    //Option 1: Anonymous Class (Simple Approach)
    //Option 2: Lambda Expression (Java 8+)
    //Option 3: Separate Class (Recommended for reusability) - see CustomerRowMapper
    //then   @Bean
    //public RowMapper<Customer> customerRowMapper() {
    //    return new CustomerRowMapper();
    //}

    public CpuMonitoring(MeterRegistry registry, JdbcTemplate jdbcTemplate) {
        this.registry = registry;
        this.jdbcTemplate = jdbcTemplate;
        osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        rowMapper = createRowMapper();

        // Register custom CPU usage metric
        registry.gauge("system.cpu.usage", osBean,
            os -> os.getSystemLoadAverage() * 100);
    }

    public List<Customer> getCustomers() {
        return registry.timer("db.query.customer.findAll")
            .record(() -> jdbcTemplate.query("SELECT * FROM customers", rowMapper));
        /*
        You can then visualize:
            Percentiles (p95, p99)
            Count
            Average latency
            Worst-case latency

        📈 5. Using Prometheus + Grafana for Categorization : Create PromQL metrics like:
            histogram_quantile(0.95, rate(db_query_customer_findAll_seconds_bucket[1m]))
            Set Grafana thresholds using color-coded performance zones:

            Red: > 1000 ms
            Orange: 500–1000 ms
            Yellow: 100–500 ms
            Green: < 100 ms

        🚀 Bonus: Automate Alerting : With Prometheus AlertManager or Grafana alerts, trigger warnings when:
            p95 query latency > 500 ms
            Query count > 100/min with > 300 ms average time
                Would you like a working Spring Boot + Prometheus example to track DB latency, with categorization? I can set that up for you.
         */
    }

    public final class CustomerInnerRowMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            return customer;
        }
    }

    public final RowMapper<Customer> customerRowMapper = (ResultSet rs, int rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        // Add more fields as needed
        return customer;
    };

    public final RowMapper<Customer> customerLambdaRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        // Add more fields as needed
        return customer;
    };

    public RowMapper<Customer> createRowMapper() {
        return (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            return customer;
        };
    }

}
/*
1. CPU Monitoring in Spring Boot

Spring Boot uses Micrometer for metrics, and you can export them to Prometheus/Azure Monitor.

Add these dependencies in pom.xml:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
*/
