package com.poc.bootbatch.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.poc.bootbatch.email.RangePartitioner;
import com.poc.bootbatch.email.StepNotificationListener;
import com.poc.bootbatch.model.RecordSO;
import com.poc.bootbatch.model.WriterSO;
import com.poc.bootbatch.processor.RecordProcessor;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty(name = "user.job.enabled")
public class UserImportConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserImportConfiguration.class);

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private JobExecutionListener listener;
    
    @Autowired
    private StepNotificationListener stepListener;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    RangePartitioner rangePartitioner = new RangePartitioner();
    
    public void importUser() throws Exception {

        LOGGER.info("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = jobLauncher.run(importUserJob(), param);

        LOGGER.info("Job finished with status :" + execution.getStatus());
    }
    
    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob-include-for-web")
                .incrementer(new RunIdIncrementer())
                .start(stepPartitioner())
                .listener(listener)
                .build();
    }

    @Bean
    @JobScope
    public Step stepPartitioner() {
        return stepBuilderFactory.get("stepPartitioner").allowStartIfComplete(false)
                .partitioner(importUserStep())
                .partitioner("stepPartitioner", rangePartitioner)
                .listener(stepListener)
                .gridSize(2)
                .build();
    }
    
    
    public Step importUserStep() {
        TaskletStep step = stepBuilderFactory.get("importUserStep1")
                .<RecordSO, WriterSO>chunk(1)
                .reader(reader(0, 0))
                .processor(processor())
                .writer(writer())
                .listener(stepListener)
                .build();
        return step;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<RecordSO> reader(
            @Value("#{stepExecutionContext[startingIndex]}") int startingIndex,
            @Value("#{stepExecutionContext[endingIndex]}") int endingIndex) {

        Map<String, Order> sortMap = new HashMap<String, Order>();
        sortMap.put("id", Order.ASCENDING);
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fromRow", startingIndex);
        paramMap.put("toRow", endingIndex);
        
        // setup queryProvider
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstName, lastname, random_num");
        queryProvider.setFromClause("from reader");
        queryProvider.setWhereClause("WHERE id BETWEEN :fromRow AND :toRow ");
        queryProvider.setSortKeys(sortMap);
        // call init to imitate spring context startup behavior
        try {
            queryProvider.init(dataSource);
        } catch (Exception e) {
           LOGGER.error("Error while init the query provider.",e);
        }
        // setup reader
        JdbcPagingItemReader<RecordSO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setQueryProvider(queryProvider);
        reader.setParameterValues(paramMap);
        reader.setRowMapper(new RowMapper<RecordSO>() {

            @Override
            public RecordSO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                if (!(resultSet.isAfterLast()) && !(resultSet.isBeforeFirst())) {
                    RecordSO recordSO = new RecordSO();
                    recordSO.setFirstName(resultSet.getString("firstName"));
                    recordSO.setLastName(resultSet.getString("lastname"));
                    recordSO.setId(resultSet.getInt("Id"));
                    recordSO.setRandomNum(resultSet.getString("random_num"));

                    LOGGER.info("RowMapper record : {}", recordSO);
                    return recordSO;
                } else {
                    LOGGER.info("Returning null from rowMapper");
                    return null;
                }
            }
        });
        reader.setPageSize(5);
        return reader;
    }

    @Bean
    public ItemProcessor<RecordSO, WriterSO> processor() {
        return new RecordProcessor();
    }

    @Bean
    public ItemWriter<WriterSO> writer() {
        JdbcBatchItemWriter<WriterSO> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setItemPreparedStatementSetter(setter());
        writer.setSql("insert into writer (id, full_name, random_num) values (?,?,?)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public ItemPreparedStatementSetter<WriterSO> setter() {
        return (item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getFullName());
            ps.setString(3, item.getRandomNum());
        };
    }
    
}