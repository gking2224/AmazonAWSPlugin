package me.gking2224.awsplugin

import org.gradle.api.Project
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials


class GradlePropertiesCredentialsProvider implements AWSCredentialsProvider {
    
    def Project project
    def logger = LoggerFactory.getLogger(GradlePropertiesCredentialsProvider.class)

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
            logger.debug("Authenticating with gradle properties")
            return new BasicAWSCredentials(project.awsAccessKeyId, project.awsSecretKey);
        }
    }

    @Override
    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
