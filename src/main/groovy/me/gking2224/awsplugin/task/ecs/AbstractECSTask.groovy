package me.gking2224.awsplugin.task.ecs;

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask

import com.amazonaws.services.ecr.AmazonECRClient
import com.amazonaws.services.ecs.AmazonECSClient

public abstract class AbstractECSTask extends AbstractAWSTask<AmazonECSClient> {

    public AbstractECSTask() {
    }
    
    AmazonECSClient getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getEcsClient()
    }
}
