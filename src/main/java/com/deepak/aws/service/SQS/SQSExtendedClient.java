package com.deepak.aws.service.SQS;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
/**
 * 
 * @author DeepakKumar
 *
 */
public class SQSExtendedClient {
	
	public  static AmazonSQS getAmazonSQSExtendedClient() {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        
        /*  Set the Amazon S3 bucket name, and then set a lifecycle rule on the
         * bucket to permanently delete objects 14 days after each object's
         * creation date.
         */
        final BucketLifecycleConfiguration.Rule expirationRule = new BucketLifecycleConfiguration.Rule();
              expirationRule.withExpirationInDays(14).withStatus("Enabled"); 
        final BucketLifecycleConfiguration lifecycleConfig = new BucketLifecycleConfiguration().withRules(expirationRule);
        // Create the bucket and allow message objects to be stored in the bucket.
        String s3BucketName = "my-bucket";
        s3.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);
        
         /*Set the Amazon SQS extended client configuration with large payload
         * support enabled.
         * */
         
        final ExtendedClientConfiguration extendedClientConfig =   new ExtendedClientConfiguration().withLargePayloadSupportEnabled(s3, s3BucketName);

       final AmazonSQS amazonSQSExtendedClient = new AmazonSQSExtendedClient(new AmazonSQSClient(new InstanceProfileCredentialsProvider(true)), extendedClientConfig);
        Region sqsRegion = Region.getRegion(Regions.US_WEST_2);
        amazonSQSExtendedClient.setRegion(sqsRegion);
        
		return amazonSQSExtendedClient;
	}
	

}
