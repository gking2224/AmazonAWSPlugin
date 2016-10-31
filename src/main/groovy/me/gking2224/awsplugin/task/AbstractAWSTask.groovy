package me.gking2224.awsplugin.task;

import org.gradle.api.DefaultTask

import com.amazonaws.AmazonWebServiceClient

public abstract class AbstractAWSTask<T> extends DefaultTask {
    
    abstract T getClient();
    
}
