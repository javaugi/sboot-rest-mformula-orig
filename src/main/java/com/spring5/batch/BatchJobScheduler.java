package com.spring5.batch;

import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
//import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchJobScheduler {

    @Autowired
    private Scheduler scheduler;

    public void scheduleDataProcessingJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(DataProcessingJob.class)
                .withIdentity("dataProcessingJob", "batchJobs")
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("dataProcessingTrigger", "batchJobs")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?")) // Daily at 2 AM
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleImmediateJob(Map<String, Object> jobData) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(ImmediateProcessingJob.class)
                .withIdentity("immediateJob-" + System.currentTimeMillis(), "immediateJobs")
                .usingJobData(new JobDataMap(jobData))
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("immediateTrigger-" + System.currentTimeMillis(), "immediateJobs")
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
