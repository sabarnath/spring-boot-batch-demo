package com.poc.bootbatch.email;


import javax.sql.DataSource;

import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
public class BatchScheduler {

    /*
    //In Memory meta data checks
    @Bean
    public ResourcelessTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactory(
            ResourcelessTransactionManager txManager) throws Exception {
        
        MapJobRepositoryFactoryBean factory = new 
                MapJobRepositoryFactoryBean(txManager);
        
        factory.afterPropertiesSet();
        
        return factory;
    }

    @Bean
    public JobRepository jobRepository(
            MapJobRepositoryFactoryBean factory) throws Exception {
        return factory.getObject();
    }*/
    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
        DataSourceTransactionManager dsTraMgr = new DataSourceTransactionManager(dataSource);
        return dsTraMgr;
    }
    @Bean
    public JobRepositoryFactoryBean jobRepositoryFactory(PlatformTransactionManager platformTransactionManager,DataSource dataSource) throws Exception{
        
        JobRepositoryFactoryBean jobRepoFac = new JobRepositoryFactoryBean();
        jobRepoFac.setDatabaseType(DatabaseType.MYSQL.toString());
        jobRepoFac.setDataSource(dataSource);
        jobRepoFac.setTransactionManager(platformTransactionManager);
        jobRepoFac.afterPropertiesSet();
        return jobRepoFac;
    }
    
    @Bean
    public JobRepository jobRepository(
            JobRepositoryFactoryBean jobRepositoryFactory) throws Exception {
        return jobRepositoryFactory.getObject();
    }

    /*@Bean
    public SimpleAsyncTaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        return simpleAsyncTaskExecutor;
    }*/
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
    
    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor taskExecutor) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(taskExecutor);
        return launcher;
    }

}