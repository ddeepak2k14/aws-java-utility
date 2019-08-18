/**
 * 
 */
package com.deepak.aws.service.SQS;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.RetryPolicy.BackoffStrategy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;


public class SQSClient {
	
	public static AmazonSQSAsync  getAmazonSQSClient() {
		// Create Client Configuration
		ClientConfiguration clientConfig = new ClientConfiguration()
			.withMaxErrorRetry(5)
			.withConnectionTTL(10_000L)
			.withTcpKeepAlive(true)
			.withRetryPolicy(new RetryPolicy(
					null, 
				new BackoffStrategy() {					
					@Override
					public long delayBeforeNextRetry(AmazonWebServiceRequest req, 
							AmazonClientException exception, int retries) {
						// Delay between retries is 10s unless it is UnknownHostException 
						// for which retry is 60s
						return exception.getCause() instanceof UnknownHostException ? 60_000L : 10_000L;
					}
				}, 10, true));
		// Create Amazon client
		final AmazonSQSAsync asyncSqsClient = AmazonSQSAsyncClientBuilder.standard().withClientConfiguration(clientConfig).build();
		//final AmazonSQSAsync asyncSqsClient = new AmazonSQSAsyncClient(new InstanceProfileCredentialsProvider(true), clientConfig);
		asyncSqsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
		asyncSqsClient.setEndpoint("https://sqs.us-west-2.amazonaws.com/id");
		
		// Buffer for request batching
		final QueueBufferConfig bufferConfig = new QueueBufferConfig();
		// Ensure visibility timeout is maintained
		bufferConfig.setVisibilityTimeoutSeconds(1200);
		// Enable long polling
		bufferConfig.setLongPoll(true);
		// Set batch parameters
//		bufferConfig.setMaxBatchOpenMs(500);
		// Set to receive messages only on demand
//		bufferConfig.setMaxDoneReceiveBatches(0);
//		bufferConfig.setMaxInflightReceiveBatches(0);
		
		return new AmazonSQSBufferedAsyncClient(asyncSqsClient, bufferConfig);
	}
	
	
	
	
	
}
