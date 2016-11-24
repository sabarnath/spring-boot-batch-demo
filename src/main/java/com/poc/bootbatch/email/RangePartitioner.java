package com.poc.bootbatch.email;

/**
 * https://blog.codecentric.de/en/2013/07/spring-batch-2-2-javaconfig-part-6-partitioning-and-multi-threaded-step/
 */

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class RangePartitioner implements Partitioner {
    private static final Logger log = LoggerFactory.getLogger(RangePartitioner.class);

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.debug("START: Partition");
        Map<String, ExecutionContext> partitionMap = new HashMap<>();
        int startingIndex = 0;
        int endingIndex = 5;
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext ctxMap = new ExecutionContext();
            ctxMap.putInt("startingIndex", startingIndex);
            ctxMap.putInt("endingIndex", endingIndex);
            startingIndex = endingIndex + 1;
            endingIndex += 5;
            partitionMap.put("Thread:-" + i, ctxMap);
        }
        log.debug("END: Created Partitions of size: " + partitionMap.size());
        return partitionMap;
    }
}
