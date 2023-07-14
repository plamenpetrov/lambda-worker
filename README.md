## General
This service is created to demonstrate how to work with AWS services using AWS SDK and Spring Boot Framework.
The lambda-worker service is configured to listen for ObjectCreated event on S3 bucket.
When such event is emitted the lambda is automatically triggered, parse the CSV file and send data to SQS topic as events.
The number of events loaded into SQS topic can be easily changed using configuration `aws.batchsize`

## Lockalstack integration
To speedup development and debug process you can use Localstack.
Localstack is a cloud service emulator that runs in a single container.

You can use the following comands to test the project locally:

- Create a bucket
   `aws s3 mb s3://medatada-bucket --endpoint-url http://localhost:4566`
- Create SQS queue
   `aws sqs create-queue --queue-name medatada-queue --endpoint-url http://localhost:4566`


- Upload jar to S3 bucket
    `aws s3 cp ../target/lambda-worker.jar s3://metadata-bucket/worker.jar --endpoint-url http://localhost:4566`

- Create function
`aws lambda create-function \
--endpoint-url http://localhost:4566 \
--memory-size 256 \
--function-name lambda-worker \
--runtime java11 \
--handler com.pp.worker.handler.LambdaHandler \
--region us-east-1 \
--code S3Bucket=metadata-bucket,S3Key=worker.jar \
--role arn:aws:iam::123456789012:role/ignoreme}`


- Register lambda to S3 bucket events
   `aws s3api put-bucket-notification-configuration --bucket metadata-bucket --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566`
- Upload file to S3 bucket

`aws s3 cp metadata/data.csv s3://backfill-img/test.csv --endpoint-url http://localhost:4566`

- Delete existing function
`aws lambda delete-function --function-name lambda-worker --endpoint-url http://localhost:4566`

## Usefull comands:
- List SQS topics
`aws sqs list-queues --endpoint-url http://localhost:4566`

- Check number of messages in the queue
`aws sqs get-queue-attributes --endpoint-url http://localhost:4566 --queue-url http://172.18.0.2:4566/000000000000/backfill-img --attribute-names ApproximateNumberOfMessages --query 'Attributes.ApproximateNumberOfMessages'`
