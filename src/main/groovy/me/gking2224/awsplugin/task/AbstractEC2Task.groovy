package me.gking2224.awsplugin.task;

import java.security.SecureRandom

import me.gking2224.awsplugin.AmazonAWSPluginExtension

import org.gradle.api.DefaultTask

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.Filter

public abstract class AbstractEC2Task extends DefaultTask {

    static final String TOKEN_PREFIX = "me.gking2224.random"
    public AbstractEC2Task() {
    }
    
    protected AmazonEC2Client getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getEc2Client()
    }
    
    def getUniqueToken() {
        def sr = new SecureRandom()
        def seed = sr.generateSeed(20)
        return "${TOKEN_PREFIX}_${System.currentTimeMillis()}${Math.abs(sr.nextInt())}"
    }
    
    def filter(String n, String... values) {
        new Filter(n, Arrays.asList(values))
    }
    
}
