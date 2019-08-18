package com.deepak.aws.service.S3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ObjectUtil {
	
	private static AmazonS3 s3 ;
	
	public S3ObjectUtil() {
		s3 = S3Util.getS3Client();
	}
	
	public void copyObject(String from_bucket,String object_name, String to_bucket) {
		 try {
	            s3.copyObject(from_bucket, object_name, to_bucket,object_name);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	}
	
	public void deleteObject(String bucket_name , String object_name) {
		try {
            s3.deleteObject(bucket_name, object_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
	}
	
	public void deleteObjects(String bucket_name , List<String> objectNameList) {
		try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucket_name)
                    .withKeys(objectNameList.toArray(new String[objectNameList.size()]));
            s3.deleteObjects(dor);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
	}
	
	public void getObject(String bucket_name , String object_name) {
		try {
            S3Object o = s3.getObject(bucket_name, object_name);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(object_name));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
	}
	
	public void listObjects(String bucket_name) {
		ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
	}
	
	public void putObject(String bucket_name , String file_path) {
		String object_name = Paths.get(file_path).getFileName().toString();
		try {
            s3.putObject(bucket_name, object_name, new File(file_path));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
	}
	
	public static void getObjectAcl(String bucket_name, String object_key) {
        System.out.println("Retrieving ACL for object: " + object_key);
        System.out.println("in bucket: " + bucket_name);
        try {
            AccessControlList acl = s3.getObjectAcl(bucket_name, object_key);
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
	
	/**
	 * 
	 * @param bucket_name ,the bucket to grant permissions on
	 * @param object_key , the object to grant permissions on If object is specified, granted permissions will be 
	 * for the object, not the bucket
	 * @param email , The email of the user to set permissions for 
	 * @param access , The permission(s) to set. Can be one of: FullControl, Read, Write, ReadAcp, WriteAcp 
	 */
	 public static void setObjectAcl(String bucket_name, String object_key, String email, String access) {
	        System.out.format("Setting %s access for %s\n", access, email);
	        System.out.println("for object: " + object_key);
	        System.out.println(" in bucket: " + bucket_name);

	        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
	        try {
	            // get the current ACL
	            AccessControlList acl = s3.getObjectAcl(bucket_name, object_key);
	            // set access for the grantee
	            EmailAddressGrantee grantee = new EmailAddressGrantee(email);
	            Permission permission = Permission.valueOf(access);
	            acl.grantPermission(grantee, permission);
	            s3.setObjectAcl(bucket_name, object_key, acl);
	        } catch (AmazonServiceException e) {
	            System.err.println(e.getErrorMessage());
	            System.exit(1);
	        }
	    }

}
