package com.example.fileProcessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class ArtifactTaskExecutorImpl implements  ArtifactTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(ArtifactTaskExecutorImpl.class);
    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private Executor taskExecutor;


    @Override
    @Async("taskExecutor")
    public void onNewTask(String task, String taskId) {

        log.info("Executor in Progress!!!");

        taskExecutor.execute( () -> {
            uploaderService.upload(task, taskId);

            uploaderService.sendMessage(task, taskId);
        });

    }
}
