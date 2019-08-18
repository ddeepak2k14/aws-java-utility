package com.deepak.aws.service.S3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;

public class BucketPolicyUtil {
	
	public static Policy getBucketPolicyFromFile(String policy_file) {
        StringBuilder file_text = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(policy_file), Charset.forName("UTF-8"));
            for (String line : lines) {
                file_text.append(line);
            }
        } catch (IOException e) {
            System.out.format("Problem reading file: \"%s\"", policy_file);
            System.out.println(e.getMessage());
        }

        // Verify the policy by trying to load it into a Policy object.
        Policy bucket_policy = null;
        try {
            bucket_policy = Policy.fromJson(file_text.toString());
        } catch (IllegalArgumentException e) {
            System.out.format("Invalid policy text in file: \"%s\"",
                    policy_file);
            System.out.println(e.getMessage());
        }

        return bucket_policy;
    }

    // Sets a public read policy on the bucket.
    public static Policy getPublicReadPolicy(String bucket_name) {
        Policy bucket_policy = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withPrincipals(Principal.AllUsers)
                        .withActions(S3Actions.GetObject)
                        .withResources(new Resource(
                                "arn:aws:s3:::" + bucket_name + "/*")));
        return bucket_policy;
    }

}
