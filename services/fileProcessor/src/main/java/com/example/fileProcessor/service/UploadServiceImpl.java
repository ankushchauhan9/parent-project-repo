package com.example.fileProcessor.service;

import com.example.fileProcessor.config.AwsConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class UploadServiceImpl implements UploaderService {

    private static final Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);

    private S3Client s3Client;

    private SqsClient sqsClient;

    private final AwsConfig awsConfig;

    @Value("${sqs.bucket.name}")
    private String bucketName;

    public UploadServiceImpl(S3Client s3Client, SqsClient sqsClient, AwsConfig awsConfig) {
        this.s3Client = s3Client;
        this.sqsClient = sqsClient;
        this.awsConfig = awsConfig;
        initializeBucket();
    }

    private void initializeBucket() {
        try{
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Bucket Empty or Flocki not reachable");
        }
    }

    @Override
    public void upload(String message, String groupId) {

        ObjectMapper objectMapper = new ObjectMapper();
        String outputJson = null;
        try {
            outputJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("ERROR CONVERTING OUTPUT TO JSON");
        }

        PutObjectRequest putObjectRequest =  PutObjectRequest.builder()
                .bucket(bucketName)
                .key(groupId)
                .contentType("application/json")
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(outputJson));
        log.info("OUTPUT FILE UPLOADED TO S3 SUCCESSFULLY!!!! {} ", groupId);
    }

    @Override
    public void sendMessage(String message, String groupId) {

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(awsConfig.getQueueUrl())
                .messageBody(message)
                .messageGroupId(groupId)
                .build();

        sqsClient.sendMessage(request);
        log.info("SQS MESSAGE SEND SUCCESSFULLY!!!!!");
    }
}
