package com.poc.bootbatch.listener;

import com.poc.bootbatch.model.WriterSO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            List<WriterSO> results = jdbcTemplate.query("SELECT id, full_name, random_num FROM writer", (rs, row) -> {
                WriterSO writerSO = new WriterSO();
                writerSO.setId(rs.getLong("id"));
                writerSO.setFullName(rs.getString("full_name"));
                writerSO.setRandomNum(rs.getString("random_num"));
                return writerSO;
            });

            for (WriterSO writerSO : results) {
                log.info("Found <" + writerSO + "> in the database.");
            }
        }
    }
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job Gonna START now .....");
        log.info("JobConfigurationName :{}",jobExecution.getJobConfigurationName());
        log.info("JobId : {}", jobExecution.getJobId() );
        log.info("JobVersion : {}", jobExecution.getVersion());
        log.info("CreateTime : {}",jobExecution.getCreateTime());
        log.info("StartTime : {}",jobExecution.getStartTime());
        log.info("ExitStatus : {}",jobExecution.getExitStatus());
        log.info("JobParameters : {}",jobExecution.getJobParameters());
        log.info("LastUpdated : {}",jobExecution.getLastUpdated());
        log.info("Status : {}",jobExecution.getStatus());
        log.info("StepExecutions : {}",jobExecution.getStepExecutions());
    }
}