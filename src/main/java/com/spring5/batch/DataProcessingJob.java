package com.spring5.batch;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DataProcessingJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        Long lastProcessedId = jobDataMap.getLong("lastProcessedId");

        try {
            processDataWithCheckpoints(lastProcessedId, context);
        } catch (Exception e) {
            // Store the last processed ID for restart
            storeCheckpoint(context, lastProcessedId);
            throw new JobExecutionException(e, true); // true = refire immediately
        }
    }

    private void processDataWithCheckpoints(Long lastProcessedId, JobExecutionContext context) {
        // Your data processing logic here
        Long currentProcessedId = lastProcessedId;

        for (int i = 0; i < 1000; i++) {
            // Process record
            currentProcessedId = processRecord(currentProcessedId + 1);

            // Store checkpoint every 100 records
            if (i % 100 == 0) {
                storeCheckpoint(context, currentProcessedId);
            }
        }

        // Clear checkpoint when job completes successfully
        clearCheckpoint(context);
    }

    private void storeCheckpoint(JobExecutionContext context, Long lastProcessedId) {
        // Store in database or job data map
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        jobDataMap.put("lastProcessedId", lastProcessedId);
    }

    private void clearCheckpoint(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        jobDataMap.remove("lastProcessedId");
    }

    private Long processRecord(Long recordId) {
        // Simulate record processing
        System.out.println("Processing record: " + recordId);
        return recordId;
    }
}
