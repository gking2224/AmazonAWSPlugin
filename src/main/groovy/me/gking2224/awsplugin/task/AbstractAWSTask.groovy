package me.gking2224.awsplugin.task;

import me.gking2224.awsplugin.AmazonAWSPluginExtension

import org.gradle.api.DefaultTask

import com.amazonaws.AmazonWebServiceClient

public abstract class AbstractAWSTask<T> extends DefaultTask {
    
    abstract T getClient();
    
    protected <CC extends AmazonWebServiceClient> CC getClient(Class<CC> clz) {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        return ext.getClient(clz)
    }
    
    protected String getRegion() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        return ext.region
    }
}
