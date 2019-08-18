package com.deepak.aws.service.S3;
import java.net.URL;
import java.util.Date;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;


/**
 * @author DeepakKumar
 * Generate a pre-signed PUT URL for uploading a file to an Amazon S3 bucket.
 */
public class S3PresignedPutUrlUtil {
		private AmazonS3 s3;
		
		public S3PresignedPutUrlUtil() {
			s3 = S3Util.getS3Client();
		}
		
		public void generatePresignedPutUrl(String bucketName,String objectName) {
	        // Set the pre-signed URL to expire after 12 hours.
	        Date expiration = new Date();
	        long expirationInMs = expiration.getTime();
	        expirationInMs += 1000 * 60 * 60 * 12;
	        expiration.setTime(expirationInMs);
	
	        try {
	            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectName)
	                    .withMethod(HttpMethod.PUT)
	                    .withExpiration(expiration);
	            URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
	            System.out.println("Generated URL: " + url.toString());
	            //Print curl command to consume URL
	            System.out.println("Example command to use URL for file upload:");
	            System.out.println("curl --request PUT --upload-file /path/to/" + objectName + " '" + url.toString() + "' -# > /dev/null");
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
			
		}
}

