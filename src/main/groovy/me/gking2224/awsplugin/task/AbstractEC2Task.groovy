package me.gking2224.awsplugin.task;

import java.security.SecureRandom

import me.gking2224.awsplugin.AmazonAWSPluginExtension

import org.gradle.api.DefaultTask

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.Filter

public abstract class AbstractEC2Task extends DefaultTask {

    static final String DEFAULT_TOKEN_PREFIX = "me.gking2224.random"
    
    def clientTokenPrefix = DEFAULT_TOKEN_PREFIX
    
    public AbstractEC2Task() {
    }
    
    protected AmazonEC2Client getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getEc2Client()
    }
    
    def getUniqueToken() {
        return project.randomString()
    }
    
    def filter(String n, String... values) {
        new Filter(n, Arrays.asList(values))
    }
    
}
