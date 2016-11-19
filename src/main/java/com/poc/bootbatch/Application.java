package com.poc.bootbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author smi-user
 * Ref : https://thetechnicaljournal.wordpress.com/2016/01/10/spring-boot-batch-starter-example/
 */

@SpringBootApplication
public class Application {

    private static Logger _log = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
        
        _log.debug("Application server getting start from here....");
        SpringApplication.run(Application.class, args);
        _log.debug("Application server started....");
        
    }
}
