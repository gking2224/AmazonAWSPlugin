package me.gking2224.awsplugin.task.autoscaling;

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient

public abstract class AbstractAutoScalingTask extends AbstractAWSTask<AmazonAutoScalingClient> {
    
    public AbstractAutoScalingTask() {
    }
    
    AmazonAutoScalingClient getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getClient(AmazonAutoScalingClient.class)
    }
}
