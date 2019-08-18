package com.deepak.aws.service.SQS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SQSMessagePublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(SQSMessagePublisher.class);
	private AmazonSQSAsync amazonSQSAsyncClient;
	private ObjectMapper objectMapper = new ObjectMapper();

	public SQSMessagePublisher() {
		// for sqs with s3 extended Client use
				//amazonSQSExtendedClient = SQSExtendedClient.getAmazonSQSExtendedClient();
				amazonSQSAsyncClient= SQSClient.getAmazonSQSClient();
		
	}
	
	public void publisMessage(Object object) {
		final GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName("SQSQueueName");
		final GetQueueUrlResult response = amazonSQSAsyncClient.getQueueUrl(request);
		String queueUrl = response.getQueueUrl();
		try {
			//Convert your object to 
			final String stringMessage = objectMapper.writeValueAsString(object);
			SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, stringMessage);
			amazonSQSAsyncClient.sendMessage(sendMessageRequest);
		} catch (Exception e) {
			LOGGER.error("SQS-Publisher", "Error while pushing data to SQS", e);
		}
	}
}
