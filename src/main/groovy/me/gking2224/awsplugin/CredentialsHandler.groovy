package me.gking2224.awsplugin

import org.slf4j.LoggerFactory

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.util.StringUtils


class CredentialsHandler implements AWSCredentialsProvider {
    
    def logger = LoggerFactory.getLogger(CredentialsHandler.class)
    
    def accessKeyId
    def secretKey
    
    public CredentialsHandler() {
    }
    
    @Override
    public AWSCredentials getCredentials() {

        if (StringUtils.isNullOrEmpty(accessKey)
                || StringUtils.isNullOrEmpty(secretKey)) {

            throw new AmazonClientException(
                    "Unable to load AWS credentials from configured credentials")
        }
        return new BasicAWSCredentials(accessKeyId, secretKey);
    }

    @Override
    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
