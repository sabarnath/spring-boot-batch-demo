package com.poc.bootbatch.email;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import({BatchScheduler.class})
@ConditionalOnProperty(name = "email.job.enabled")
public class EmailSenderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderConfiguration.class);

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private JobExecutionListener listener;


    RangePartitioner rangePartitioner = new RangePartitioner();


    public void emailSender() throws Exception {

        log.info("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = jobLauncher.run(emailSenderJob(), param);

        log.info("Job finished with status :" + execution.getStatus());
    }


    @Bean
    public Job emailSenderJob() {
        return jobBuilderFactory.get("emailSenderJob").incrementer(new RunIdIncrementer())
                .listener(listener).flow(emailSenderJobStep()).end().build();
    }


    @Bean
    public Step emailSenderJobStep() {

        TaskletStep step = stepBuilderFactory.get("emailSenderJobStep").allowStartIfComplete(false)
                .<List<String>, List<String>>chunk(5)
                .reader((ItemReader<List<String>>) emailSenderJobReader())
                .processor((ItemProcessor<? super List<String>, ? extends List<String>>) emailSenderJobProcessor())
                .writer((ItemWriter<List<String>>) emailSenderJobWriter()).build();
        return step;
    }


    @Bean
    public ItemReader<List<String>> emailSenderJobReader()  {

        return new EmailSenderReader();
    }

    @Bean
    public EmailSenderProcessor emailSenderJobProcessor() {
        return new EmailSenderProcessor();
    }

    @Bean
    public ItemWriter<List<String>> emailSenderJobWriter() {
        return new EmailSenderWriter();
    }

 
}
