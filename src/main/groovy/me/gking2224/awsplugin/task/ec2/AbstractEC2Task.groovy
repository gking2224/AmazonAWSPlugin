package me.gking2224.awsplugin.task.ec2;

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.Filter

public abstract class AbstractEC2Task extends AbstractAWSTask<AmazonEC2Client> {

    static final String DEFAULT_TOKEN_PREFIX = "me.gking2224.random"
    
    def clientTokenPrefix = DEFAULT_TOKEN_PREFIX
    
    public AbstractEC2Task() {
    }
    
    AmazonEC2Client getClient() {
        return super.getClient(AmazonEC2Client.class)
    }
    
    def getUniqueToken() {
        return project.randomString()
    }
    
    def filter(String n, String... values) {
        new Filter(n, Arrays.asList(values))
    }
    
}
