/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.batch;

import com.spring5.entity.Customer;
import java.util.Collections;
import java.util.Date;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LargeVolumeBatchConfig {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TaskExecutor taskExecutor;

    @Bean
    public Job largeVolumeJob() {
        return new JobBuilder("largeVolumeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkProcessingStep())
                .next(cleanupStep())
                .build();
    }

    @Bean
    public Step chunkProcessingStep() {
        return new StepBuilder("chunkProcessingStep", jobRepository)
                .<Customer, ProcessedCustomer>chunk(1000, platformTransactionManager) // Process 1000 records at a time
                .reader(customerReader())
                .processor(customerProcessor())
                .writer(customerWriter())
                .faultTolerant()
                .skipLimit(1000)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(Exception.class)
                .taskExecutor(taskExecutor)
                //.throttleLimit(5)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Customer> customerReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource());
        reader.setPageSize(1000);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("SELECT id, name, email, status");
        queryProvider.setFromClause("FROM customers");
        queryProvider.setWhereClause("WHERE processed = false");
        queryProvider.setSortKeys(Collections.singletonMap("id", Order.ASCENDING));

        reader.setQueryProvider(queryProvider);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
        return reader;
    }

    @Bean
    public ItemProcessor<Customer, ProcessedCustomer> customerProcessor() {
        return customer -> {
            // Business logic processing
            ProcessedCustomer processed = new ProcessedCustomer();
            processed.setId(customer.getId());
            processed.setName(customer.getName().toUpperCase());
            processed.setEmail(customer.getEmail());
            processed.setProcessedDate(new Date());
            processed.setStatus("PROCESSED");

            // Simulate processing time
            Thread.sleep(10);

            return processed;
        };
    }

    @Bean
    public ItemWriter<ProcessedCustomer> customerWriter() {
        return items -> {
            for (ProcessedCustomer customer : items) {
                // Batch insert or update logic
                System.out.println("Writing customer: " + customer.getName());
            }
            // Actual database write would go here
        };
    }

    @Bean
    public Step cleanupStep() {
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet(cleanupTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Tasklet cleanupTasklet() {
        return (contribution, chunkContext) -> {
            // Cleanup logic - mark records as processed, archive, etc.
            System.out.println("Cleaning up after batch processing");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public DataSource dataSource() {
        // Configure your data source
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }
}
