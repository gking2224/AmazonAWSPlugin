package me.gking2224.awsplugin

import static com.amazonaws.SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY
import static com.amazonaws.SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.util.StringUtils


class CredentialsHandler implements AWSCredentialsProvider {
    
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
        println "Using AWS credentials from config block"
        return new BasicAWSCredentials(accessKeyId, secretKey);
    }

    @Override
    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
