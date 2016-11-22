package com.poc.bootbatch.email;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * @author 
 *
 */
public class EmailSenderWriter implements ItemWriter<List<String>> {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderWriter.class);


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
     */
    @Override
    public void write(List<? extends List<String>> emailTskList) {
        log.info("EmailSenderWriter started for send the mail ....");
    }
}
