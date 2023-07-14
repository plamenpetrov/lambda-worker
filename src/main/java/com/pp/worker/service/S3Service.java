package com.pp.worker.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.List;

@Service
public class S3Service {

    public final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    public InputStream readFileFromS3Event(S3Event s3Event) {
        S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord = getS3EventNotificationRecord(s3Event);
        String srcKey = s3EventNotificationRecord.getS3().getObject().getUrlDecodedKey();
        String srcBucket = s3EventNotificationRecord.getS3().getBucket().getName();
        logger.info("Bucket: {}, file name: {}", srcBucket, srcKey);

        Region region = Region.of(Regions.US_WEST_1.getName());
        S3Client s3Client = S3Client.builder().region(region).build();
        GetObjectRequest request = GetObjectRequest.builder().bucket(srcBucket).key(srcKey).build();

        return s3Client.getObject(request);
    }

    protected S3EventNotification.S3EventNotificationRecord getS3EventNotificationRecord(S3Event s3Event) {
        List<S3EventNotification.S3EventNotificationRecord> records = s3Event.getRecords();
        records.sort((a, b) -> b.getEventTime().compareTo(a.getEventTime()));

        return records.get(0);
    }
}
