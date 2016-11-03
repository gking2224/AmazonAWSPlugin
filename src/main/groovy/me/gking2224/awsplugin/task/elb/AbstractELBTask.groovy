package me.gking2224.awsplugin.task.elb;

import me.gking2224.awsplugin.AmazonAWSPluginExtension
import me.gking2224.awsplugin.task.AbstractAWSTask

import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClient

public abstract class AbstractELBTask extends AbstractAWSTask<AmazonElasticLoadBalancingClient> {

    public AbstractELBTask() {
    }
    
    AmazonElasticLoadBalancingClient getClient() {
        super.getClient(AmazonElasticLoadBalancingClient.class)
    }
    
}
