package com.poc.bootbatch.email;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


/**
 * @author 
 *
 */
public class EmailSenderProcessor implements ItemProcessor<List<String>, List<String>> {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderProcessor.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
     */
    @Override
    public List<String> process(List<String> item) throws Exception {
        log.debug(
                "EmailSenderProcessor Object count::>>> " + item.size());
        return item;
    }


}

