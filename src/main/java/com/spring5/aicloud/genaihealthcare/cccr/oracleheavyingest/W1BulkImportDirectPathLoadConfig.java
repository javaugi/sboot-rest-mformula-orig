/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
//import org.apache.spark.sql.execution.columnar.ARRAY;
//import org.apache.spark.sql.execution.columnar.ARRAY;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
/*
Key Points:
    Method Signature: The write method should accept List<? extends ClaimRecord> and not declare checked exceptions
    Lambda Syntax: Use lambda expressions for cleaner code in Java 8+
    Error Handling: Always include proper error handling for batch operations
    Resource Management: Use try-with-resources for proper connection management
    Performance: Consider using JdbcBatchItemWriter for better performance out-of-the-box
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class W1BulkImportDirectPathLoadConfig {

    /*
    1. Bulk Import Strategies
        Direct Path Load (SQL*Loader External Tables)
        Use Case: Initial data loads, large batch imports
     */
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public JdbcCursorItemReader<ClaimRecord> claimReader() {
        return new JdbcCursorItemReaderBuilder<ClaimRecord>()
            .dataSource(dataSource)
            .name("claimReader")
            .sql("SELECT claim_id, patient_id, amount FROM external_claims")
            .rowMapper(new BeanPropertyRowMapper<>(ClaimRecord.class))
            .fetchSize(10000)
            .build();
    }

    @Bean
    public ItemWriter<ClaimRecord> directPathItemWriter() {
        return items -> {
            String sql = "INSERT /*+ APPEND */ INTO claims_bulk (claim_id, patient_id, amount) "
                + "VALUES (?, ?, ?)";

            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

                // Use direct path insert (APPEND hint)
                for (ClaimRecord item : items) {
                    ps.setString(1, item.getClaimId());
                    ps.setString(2, item.getPatientId());
                    ps.setBigDecimal(3, item.getAmount());
                    ps.addBatch();
                }

                ps.executeBatch();

            } catch (SQLException e) {
                throw new RuntimeException("Direct path insert failed", e);
            }
        };
    }


    //Solution 2: Using JdbcBatchItemWriter (Recommended)
    @Bean
    public JdbcBatchItemWriter<ClaimRecord> directPathBatchItemWriter() {
        String sql = "INSERT /*+ APPEND */ INTO claims_bulk (claim_id, patient_id, amount) "
            + "VALUES (:claimId, :patientId, :amount)";

        JdbcBatchItemWriter<ClaimRecord> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(sql);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        // For direct path inserts, we need to handle differently
        // This approach uses regular batch insert
        return writer;
    }

    //Solution 3: Oracle-Specific Direct Path Writer
    @Bean
    public ItemWriter<ClaimRecord> oracleDirectPathWriter() {
        return items -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "INSERT /*+ APPEND */ INTO claims_bulk (claim_id, patient_id, amount) VALUES (?, ?, ?)")) {

                // Disable auto-commit for batch processing
                conn.setAutoCommit(false);

                for (ClaimRecord item : items) {
                    ps.setString(1, item.getClaimId());
                    ps.setString(2, item.getPatientId());
                    ps.setBigDecimal(3, item.getAmount());
                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                throw new RuntimeException("Failed to execute batch insert", e);
            }
        };
    }

    @Bean
    public Step bulkImportStep(ItemReader<ClaimRecord> reader,
        ItemWriter<ClaimRecord> writer) {
        return new StepBuilder("bulkImportStep", jobRepository)
            .<ClaimRecord, ClaimRecord>chunk(1000, transactionManager)
            .reader(reader)
            .writer(writer)
            .build();
    }

    @Bean
    public Job bulkImportJob(Step bulkImportStep) {
        return new JobBuilder("bulkImportJob", jobRepository)
            .start(bulkImportStep)
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("Starting bulk import job...");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("Bulk import job completed with status: "
                    + jobExecution.getStatus());
            }
        };
    }

}
