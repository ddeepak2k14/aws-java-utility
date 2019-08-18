package com.deepak.aws.service.S3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Util {
	private static AmazonS3 s3 ;
	
	public static AmazonS3 getS3Client() {
		/*different way to create client
		aws document https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/credentials.html
		1. to create client using access key and secret key explicitly
		String accessKey = "aws-access-key";
		String secretKey = "aws-secret-access-key";
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		or 
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		2. Using default credential provider chain
		s3 = new AmazonS3Client();
		*/
		s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();		
		
		return s3;
	}
	
	
	
	
	
	
	
}
