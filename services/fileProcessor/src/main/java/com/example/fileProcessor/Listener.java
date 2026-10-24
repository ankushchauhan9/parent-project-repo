package com.example.fileProcessor;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Listener {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @SqsListener("${sqs.listener.queue.name}")
    public void readMessage(String message) {
        log.info("SQS MESSAGE RECEIVED : ");
        System.out.println("Message : "  + message);
    }

}
