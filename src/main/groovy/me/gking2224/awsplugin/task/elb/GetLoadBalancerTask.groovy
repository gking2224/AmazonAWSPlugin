package me.gking2224.awsplugin.task.elb

import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;



class GetLoadBalancerTask extends AbstractELBTask {

    def loadBalancers
    
    public GetLoadBalancerTask() {
    }

    @TaskAction
    def getLoadBalancer() {
        project.dryRunExecute("GetLoadBalancerTask", {
            loadBalancers = getClient().describeLoadBalancers(new DescribeLoadBalancersRequest()).getLoadBalancers()
        }, {
            logger.debug("DryRun: GetLoadBalancer")
        })
        
    }
}
