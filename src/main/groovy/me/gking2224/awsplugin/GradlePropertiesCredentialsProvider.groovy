package me.gking2224.awsplugin

import org.gradle.api.Project

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials


class GradlePropertiesCredentialsProvider implements AWSCredentialsProvider {
    
    def Project project

    public GradlePropertiesCredentialsProvider(Project p) {
        this.project = p
    }
        
    @Override
    public AWSCredentials getCredentials() {
        if (!project.hasProperty("awsAccessKeyId") || !project.hasProperty("awsSecretKey")) {

            throw new AmazonClientException(
                    "Unable to load AWS credentials from properties")
        }
        else {
            println "Using AWS credentials from gradle properties"
            return new BasicAWSCredentials(project.accessKeyId, project.secretKey);
        }
    }

    @Override
    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
