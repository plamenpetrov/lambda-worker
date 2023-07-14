package com.pp.worker.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.pp.worker.service.MetadataService;

public class LambdaHandler implements RequestHandler<S3Event, String> {

    public MetadataService lambdaService;

    public void LambdaHandler(MetadataService lambdaService) {
        this.lambdaService = lambdaService;
    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        return lambdaService.processCSVFile(s3Event);
    }
}
