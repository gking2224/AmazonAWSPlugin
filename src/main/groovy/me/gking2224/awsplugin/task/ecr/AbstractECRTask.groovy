package me.gking2224.awsplugin.task.ecr;

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask

import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ecr.AmazonECRClient

public abstract class AbstractECRTask extends AbstractAWSTask<AmazonECRClient> {

    public AbstractEC2Task() {
    }
    
    AmazonECRClient getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getEcrClient()
    }
}
