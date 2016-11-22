package com.poc.bootbatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.bootbatch.config.UserImportConfiguration;
import com.poc.bootbatch.email.EmailSenderConfiguration;

@RestController
public class BatchRestController {

    private static Logger _log = LoggerFactory.getLogger(BatchRestController.class);


    @Autowired
    EmailSenderConfiguration emailSenderConfiguration;
    
    @Autowired
    UserImportConfiguration userImportConfiguration;
    
    @RequestMapping("/hello-world")
    public @ResponseBody String sayHello() {
        _log.info("BatchRestController entered...");
        return "Hello All : Welcome to BatchReestController.....:)";
    }

    @RequestMapping("/runUserImport")
    public @ResponseBody String runUserImport(){
        String message = "";
        try {
            userImportConfiguration.importUser();
            message = "SUCCESS";
        } catch (Exception e) {
           _log.error("Error while run the userImport....",e);
           message = "FAILURE";
        }
        return message;
    }
    
    @RequestMapping("/runEmailSender")
    public @ResponseBody String runEmailSender(){
        String message = "";
        try {
            emailSenderConfiguration.emailSender();
            message = "SUCCESS";
        } catch (Exception e) {
           _log.error("Error while run the emailsender....",e);
           message = "FAILURE";
        }
        return message;
    }
}
