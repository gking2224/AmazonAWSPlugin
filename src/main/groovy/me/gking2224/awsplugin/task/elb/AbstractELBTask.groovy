package me.gking2224.awsplugin.task.elb;

import java.security.SecureRandom

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask;

import org.gradle.api.DefaultTask

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;

public abstract class AbstractELBTask extends AbstractAWSTask<AmazonElasticLoadBalancingClient> {

    static final String DEFAULT_TOKEN_PREFIX = "me.gking2224.random"
    
    def clientTokenPrefix = DEFAULT_TOKEN_PREFIX
    
    public AbstractEC2Task() {
    }
    
    AmazonElasticLoadBalancingClient getClient() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        ext.getElbClient()
    }
    
    def getUniqueToken() {
        return project.randomString()
    }
    
    def filter(String n, String... values) {
        new Filter(n, Arrays.asList(values))
    }
    
}
