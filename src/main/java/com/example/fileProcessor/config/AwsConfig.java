package com.example.fileProcessor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

import java.net.URI;
import java.util.concurrent.Executor;

@Configuration
public class AwsConfig {

    private static final Logger log = LoggerFactory.getLogger(AwsConfig.class);
    private static final String FLOCKI_ENDPOINT = "http://localhost:4566";

    @Value("${sqs.region}")
    private String awsRegion;

    // Parameter names in SSM (same as used in FetchParameter)
    private static final String ACCESS_KEY_PARAM = "/dev/spring.cloud.aws.credentials.access-key";
    private static final String SECRET_KEY_PARAM = "/dev/spring.cloud.aws.credentials.secret-key";
    private static final String QUEUE_URL_PARAM = "/dev/sqs/url";


    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(FLOCKI_ENDPOINT))
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider())
                .forcePathStyle(true) // Required for local AWS emulators like Floci/LocalStack
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create(FLOCKI_ENDPOINT))
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider())
                .build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(FLOCKI_ENDPOINT))
                .region(Region.of(awsRegion))
                .credentialsProvider(credentialsProvider())
                .build();
    }

    @Bean("taskExecutor")
    public Executor taskExecutor(@Value("${executor.core.pool.size}") int poolSize,
                                 @Value("${executor.queue.capacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("TASK-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder()
                .endpointOverride(URI.create(FLOCKI_ENDPOINT))
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create("test", "test")))
                .build();
    }

    // Build a credentials provider by reading access/secret from SSM. If fetching fails, fall back to test/test.
    private StaticCredentialsProvider credentialsProvider() {
        try {
            String accessKey = fetchParameter(ACCESS_KEY_PARAM);
            String secretKey = fetchParameter(SECRET_KEY_PARAM);
            if (accessKey != null && secretKey != null) {
                return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
            }
        } catch (Exception e) {
            log.warn("Failed to load credentials from SSM, falling back to defaults", e);
        }
        // Fallback for local dev / LocalStack
        log.info("Using fallback AWS credentials");
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
    }

    private String fetchParameter(String name) {
        // IMPORTANT: do not call ssmClient() here (that would call credentialsProvider() and create a cycle).
        // Build a temporary SsmClient using fallback credentials to fetch the parameter.
        try (SsmClient temp = SsmClient.builder()
                .endpointOverride(URI.create(FLOCKI_ENDPOINT))
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .build()) {

            GetParameterRequest request = GetParameterRequest.builder()
                    .name(name)
                    .withDecryption(true)
                    .build();
            return temp.getParameter(request).parameter().value();
        } catch (Exception e) {
            log.debug("Unable to fetch parameter {} from SSM: {}", name, e.getMessage());
            return null;
        }
    }

    public String getQueueUrl() {
        return fetchParameter(QUEUE_URL_PARAM);
    }

}
