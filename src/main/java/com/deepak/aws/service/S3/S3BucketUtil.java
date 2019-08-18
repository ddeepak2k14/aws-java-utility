package com.deepak.aws.service.S3;

import java.util.Iterator;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

public class S3BucketUtil {
	private static AmazonS3 s3 ;
	
		public S3BucketUtil() {
			s3 = S3Util.getS3Client();
		}
	
		public Bucket createBucket(String bucket_name) {
	        Bucket b = null;
	        if (s3.doesBucketExistV2(bucket_name)) {
	            System.out.format("Bucket %s already exists.\n", bucket_name);
	            b = getBucket(bucket_name);
	        } else {
	            try {
	                b = s3.createBucket(bucket_name);
	            } catch (AmazonS3Exception e) {
	                System.err.println(e.getErrorMessage());
	            }
	        }
	        return b;
	    }
		
		public  List<Bucket> listBucket() {
			List<Bucket> buckets = s3.listBuckets();
	        System.out.println("Your Amazon S3 buckets are:");
	        for (Bucket b : buckets) {
	            System.out.println("* " + b.getName());
	        }
	        return buckets;
		}
	
		public  Bucket getBucket(String bucket_name) {
	        Bucket named_bucket = null;
	        List<Bucket> buckets = s3.listBuckets();
	        for (Bucket b : buckets) {
	            if (b.getName().equals(bucket_name)) {
	                named_bucket = b;
	            }
	        }
	        return named_bucket;
	    }

	    public void deleteNonEmptyBucket(String bucket_name) {
			try {
				
				 // Delete non-empty bucket
		        // To delete a bucket, all the objects in the bucket should be deleted first
	            System.out.println(" - removing objects from bucket");
	            ObjectListing object_listing = s3.listObjects(bucket_name);
	            while (true) {
	                for (Iterator<?> iterator =
	                     object_listing.getObjectSummaries().iterator();
	                     iterator.hasNext(); ) {
	                    S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
	                    s3.deleteObject(bucket_name, summary.getKey());
	                }

	                // more object_listing to retrieve?
	                if (object_listing.isTruncated()) {
	                    object_listing = s3.listNextBatchOfObjects(object_listing);
	                } else {
	                    break;
	                }
	            }

	            System.out.println(" - removing versions from bucket");
	            VersionListing version_listing = s3.listVersions(
	                    new ListVersionsRequest().withBucketName(bucket_name));
	            while (true) {
	                for (Iterator<?> iterator =
	                     version_listing.getVersionSummaries().iterator();
	                     iterator.hasNext(); ) {
	                    S3VersionSummary vs = (S3VersionSummary) iterator.next();
	                    s3.deleteVersion(
	                            bucket_name, vs.getKey(), vs.getVersionId());
	                }

	                if (version_listing.isTruncated()) {
	                    version_listing = s3.listNextBatchOfVersions(
	                            version_listing);
	                } else {
	                    break;
	                }
	            }

	            System.out.println(" OK, bucket ready to delete!");
	            s3.deleteBucket(bucket_name);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
		}
	    
	    public void deleteBucketPolicy(String bucket_name) {
	    	try {
	            s3.deleteBucketPolicy(bucket_name);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }
	    
	    public void getBucketPolicy(String bucket_name) {
	    	String policy_text = null;
	    	 try {
	             BucketPolicy bucket_policy = s3.getBucketPolicy(bucket_name);
	             policy_text = bucket_policy.getPolicyText();
	         } catch (AmazonServiceException e) {
	             System.err.println(e.getErrorMessage());
	             System.exit(1);
	         }

	         if (policy_text == null) {
	             System.out.println("The specified bucket has no bucket policy.");
	         } else {
	             System.out.println("Returned policy:");
	             System.out.println(policy_text);
	         }
	    }
	    
	    public static void setBucketPolicy(String bucket_name, String policyFile) {
	    	String policyText=null;
	    	//reading bucket policy from json file
	    	policyText = BucketPolicyUtil.getBucketPolicyFromFile(policyFile).toJson();
	    	// Sets a public read policy on the bucket.
	    	//policyText = BucketPolicyUtil.getPublicReadPolicy(bucket_name).toJson();
	        try {
	            s3.setBucketPolicy(bucket_name, policyText);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }
	    
	    /**
	     * 
	     * @param bucket_name, the bucket to grant permissions on
	     * @param email, The email of the user to set permissions for
	     * @param access, The permission(s) to set. Can be one of: FullControl, Read, Write, ReadAcp, WriteAcp 
	     */
	    
	    public static void setBucketAcl(String bucket_name, String email, String access) {
	        System.out.format("Setting %s access for %s\n", access, email);
	        System.out.println("on bucket: " + bucket_name);
	        try {
	            // get the current ACL
	            AccessControlList acl = s3.getBucketAcl(bucket_name);
	            // set access for the grantee
	            EmailAddressGrantee grantee = new EmailAddressGrantee(email);
	            Permission permission = Permission.valueOf(access);
	            acl.grantPermission(grantee, permission);
	            s3.setBucketAcl(bucket_name, acl);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }
	    
	    public static void getBucketAcl(String bucketName) {
	        System.out.println("Retrieving ACL for bucket: " + bucketName);
	        try {
	            AccessControlList acl = s3.getBucketAcl(bucketName);
	            List<Grant> grants = acl.getGrantsAsList();
	            for (Grant grant : grants) {
	                System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(),
	                        grant.getPermission().toString());
	            }
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }
	 
	

}
