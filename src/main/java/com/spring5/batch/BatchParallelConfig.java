package com.spring5.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchParallelConfig {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job jobBuilderFactory(JobRepository jobRepository, Step myStep) {
        return new JobBuilder("parallelProcessingJob", jobRepository)
                .start(myStep)
                .build();
    }

    @Bean
    public Step myStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<String> reader, ItemWriter<String> writer) {
        return new StepBuilder("myStep", jobRepository)
                .<String, String>chunk(10, transactionManager) // transactionManager is now explicitly passed
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Flow splitFlow() {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(flow1(), flow2(), flow3())
                .build();
    }

    @Bean
    public Flow flow1() {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(dataValidationStep())
                .build();
    }

    @Bean
    public Flow flow2() {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(dataTransformationStep())
                .build();
    }

    @Bean
    public Flow flow3() {
        return new FlowBuilder<SimpleFlow>("flow3")
                .start(dataEnrichmentStep())
                .build();
    }

    @Bean
    public Step dataValidationStep() {
        return new StepBuilder("dataValidationStep", jobRepository)
                .tasklet(dataValidationTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step dataTransformationStep() {
        return new StepBuilder("dataTransformationStep", jobRepository)
                .tasklet(dataTransformationTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step dataEnrichmentStep() {
        return new StepBuilder("dataEnrichmentStep", jobRepository)
                .tasklet(dataEnrichmentTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step aggregationStep() {
        return new StepBuilder("aggregationStep", jobRepository)
                .tasklet(aggregationTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("parallel-step-");
        executor.initialize();
        return executor;
    }

    // Tasklet implementations
    @Bean
    public Tasklet dataValidationTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Validating data on thread: " + Thread.currentThread().getName());
            // Validation logic
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet dataEnrichmentTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Validating data on thread: " + Thread.currentThread().getName());
            // Validation logic
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet dataTransformationTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Validating data on thread: " + Thread.currentThread().getName());
            // Validation logic
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet aggregationTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Aggregating results from parallel steps");
            // Aggregation logic
            return RepeatStatus.FINISHED;
        };
    }
}
