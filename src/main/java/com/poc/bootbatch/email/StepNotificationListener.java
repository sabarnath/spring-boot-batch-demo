package com.poc.bootbatch.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;



public class StepNotificationListener extends StepExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(StepNotificationListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.STARTED){
            log.info(" !! Step Execution Started !! {}",stepExecution.getStepName());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.COMPLETED){
            log.info(" !! Step Execution completed !! {}",stepExecution.getStepName());
        }
        return null;
    }
   
}
