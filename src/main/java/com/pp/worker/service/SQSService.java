package com.pp.worker.service;

import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.pp.worker.dto.MetadataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class SQSService {

    public final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    private Gson gson;

    public void SQSService(Gson gson) {
        this.gson = gson;
    }

    public void sendBatchToSqs(String queue, int delay, List<MetadataDto> messageData) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String payload = gson.toJson(messageData);
            String queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
            SendMessageRequest messageRequest = getSendMessageRequest(delay, queueUrl, payload);

            sqs.sendMessage(messageRequest);
            logger.info("Sending messages to SQS asynchronously");

            return Thread.currentThread().getName();
        });
        completableFuture.get();
    }

    private SendMessageRequest getSendMessageRequest(int delay, String queueUrl, String payload) {
        return new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(payload)
                .withDelaySeconds(delay);
    }

}
