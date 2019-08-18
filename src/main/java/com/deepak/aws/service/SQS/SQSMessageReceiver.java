package com.deepak.aws.service.SQS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SQSMessageReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SQSMessageReceiver.class);
	AmazonSQSAsync amazonSQSAsyncClient;
	
	
	public SQSMessageReceiver(){
		// for sqs with s3 extended Client use
		//amazonSQSExtendedClient = SQSExtendedClient.getAmazonSQSExtendedClient();
		amazonSQSAsyncClient= SQSClient.getAmazonSQSClient();
	}
	private ReceiveMessageResult getMessagesFromSQS(String queueUrl) {
		
		
		try {
			// Create new request and fetch data from Amazon SQS queue
			//max no of message is 5, you can configure it as per your requirement,ideally it should be 1
			final ReceiveMessageResult receiveResult = amazonSQSAsyncClient
					.receiveMessage(new ReceiveMessageRequest().withMaxNumberOfMessages(5).withQueueUrl(queueUrl));
			return receiveResult;
		} catch (Exception e) {
			LOGGER.error("Error while fetching data from SQS", e);
		}
		return null;
	}
	
	public void processSQSMessage() throws Exception {
		final GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName("SQSQueueName");
		final GetQueueUrlResult response = amazonSQSAsyncClient.getQueueUrl(request);
		String queueUrl = response.getQueueUrl();
		ReceiveMessageResult message = getMessagesFromSQS(queueUrl);
		for ( Message mess : message.getMessages()) {
			String messageBody = mess.getBody();
			//process message,use object mapper to conver it to java object like this
			//Object obj = new ObjectMapper().readValue(messageBody, Object.class);
			System.out.println(messageBody);
			String messageReceiptHandle = mess.getReceiptHandle();
			amazonSQSAsyncClient.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
			
		};
	}
	
	
	

}
