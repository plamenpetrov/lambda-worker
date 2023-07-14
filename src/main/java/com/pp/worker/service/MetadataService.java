package com.pp.worker.service;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.pp.worker.infrastructure.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MetadataService {

    public final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    private Config config;

    private CSVService csvService;

    private S3Service s3Service;

    public void MetadataService(Config config, CSVService csvService, S3Service s3Service) {
        this.config = config;
        this.csvService = csvService;
        this.s3Service = s3Service;
    }

    public String processCSVFile(S3Event s3Event) {
        InputStream stream = getInputStream(s3Event);
        int processedRestaurantIds = csvService.readFileAndSendMessageToTopic(stream, config.getQueue(), config.getBatchSize(), config.getDelay());

        return "Successfully processed " + processedRestaurantIds + " events";
    }

    public InputStream getInputStream(S3Event s3Event) {
        return s3Service.readFileFromS3Event(s3Event);
    }
}
