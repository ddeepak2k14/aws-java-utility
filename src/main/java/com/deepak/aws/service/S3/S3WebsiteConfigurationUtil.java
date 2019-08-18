package com.deepak.aws.service.S3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;

public class S3WebsiteConfigurationUtil {
	  private static AmazonS3 s3 ;
	  public S3WebsiteConfigurationUtil() {
		   s3 = S3Util.getS3Client();
	  }
	
	  public static void deleteWebsiteConfig(String bucket_name) {
	        try {
	            s3.deleteBucketWebsiteConfiguration(bucket_name);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.out.println("Failed to delete website configuration!");
	            System.exit(1);
	        }
	    }
	  
	  public void getWebsiteConfig(String bucketName) {
		  try {
	            BucketWebsiteConfiguration config =
	                    s3.getBucketWebsiteConfiguration(bucketName);
	            if (config == null) {
	                System.out.println("No website configuration found!");
	            } else {
	                System.out.format("Index document: %s\n",
	                        config.getIndexDocumentSuffix());
	                System.out.format("Error document: %s\n",
	                        config.getErrorDocument());
	            }
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.out.println("Failed to get website configuration!");
	            System.exit(1);
	        }
	  }
	  
	  /**
	   * 
	   * @param bucket_name,the bucket to set the web site configuration
	   * @param index_doc,(optional) the index document, ex. 'index.html',If not specified, 'index.html' will be set
	   * @param error_doc , (optional) the error document, ex. 'notfound.html' If not specified, no error doc will be set
	   */
	  public static void setWebsiteConfig(String bucket_name, String index_doc, String error_doc) {
	        BucketWebsiteConfiguration website_config = null;
	        if (index_doc == null) {
	            website_config = new BucketWebsiteConfiguration();
	        } else if (error_doc == null) {
	            website_config = new BucketWebsiteConfiguration(index_doc);
	        } else {
	            website_config = new BucketWebsiteConfiguration(index_doc, error_doc);
	        }
	        try {
	            s3.setBucketWebsiteConfiguration(bucket_name, website_config);
	        } catch (AmazonServiceException e) {
	            System.out.format(
	                    "Failed to set website configuration for bucket '%s'!\n",
	                    bucket_name);
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }

}
