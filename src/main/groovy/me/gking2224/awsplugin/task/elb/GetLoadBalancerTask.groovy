package me.gking2224.awsplugin.task.elb

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult



class GetLoadBalancerTask extends AbstractELBTask {

    public GetLoadBalancerTask() {
    }

    @TaskAction
    def getLoadBalancer() {
        project.dryRunExecute("GetLogin", {
            
            DescribeLoadBalancersResult res = getClient().describeLoadBalancers()
            res.getLoadBalancerDescriptions().each {
                logger.info it
            }
        }, {
            logger.debug("DryRun: GetLoadBalancer")
        })
        
    }
}
