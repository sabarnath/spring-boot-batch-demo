package com.poc.bootbatch.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;



@Component
public class StepNotificationListener extends StepExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(StepNotificationListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.STARTED){
            log.info(" !! Step Execution Started !!");
        }
    }

   
}
