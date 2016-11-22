package com.poc.bootbatch.email;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

public class EmailSenderReader implements ItemReader<List<String>> {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderReader.class);


    private static boolean collected = false;

    public List<String> read() {
        List<String> emailTaskLogIds = new ArrayList<>();
        if (!collected) {
            try {
                collected = true;
                log.info("Enter into EmailSenderReader....");
                
            } catch (Exception e) {
                log.error("Error in EmailSenderReader ", e);
            }
        } else {
            collected = false;
            return null;
        }
        return emailTaskLogIds;
    }
}
