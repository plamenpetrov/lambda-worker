package com.pp.worker.service;

import com.pp.worker.dto.MetadataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CSVService {

    public final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    private SQSService sqsService;

    public void CSVService(SQSService sqsService) {
        this.sqsService = sqsService;
    }

    public int readFileAndSendMessageToTopic(InputStream inputStream, String queue, int batchSize, int delay) {
        int lineCount = 0;
        int batchCount = 0;
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            List<MetadataDto> messageData = new ArrayList<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] currRecord = line.split(",");
                MetadataDto MetadataDto = new MetadataDto(currRecord[0], currRecord[1], currRecord[2]);
                messageData.add(MetadataDto);

                lineCount++;
                if (lineCount % batchSize == 0) {
                    // Perform batch processing logic after reading BATCH_SIZE lines
                    sqsService.sendBatchToSqs(queue, delay, messageData);
                    messageData.clear();
                    batchCount++;
                    logger.info("Batch {}", batchCount);
                    logger.info("Restaurant ids sent {}", lineCount);
                }
            }
            if (lineCount % batchSize != 0) {
                // Process the remaining lines if they don't form a complete batch
                sqsService.sendBatchToSqs(queue, delay, messageData);
                batchCount++;
                logger.info("Last batch {}", batchCount);
                logger.info("Restaurant ids sent {}", lineCount);
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            logger.error("Something went wrong while processing the file - {}", e.getMessage());
        }
        logger.info("Success! {} restaurant ids were sent to the SQS topic", lineCount);

        return lineCount;
    }


}
