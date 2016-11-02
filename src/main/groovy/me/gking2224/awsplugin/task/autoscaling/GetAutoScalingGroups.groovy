package me.gking2224.awsplugin.task.autoscaling

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult



class GetAutoScalingGroups extends AbstractAutoScalingTask {

    def names
    
    def autoScalingGroups

    public GetAutoScalingGroups() {
    }

    @TaskAction
    def doTask() {
        
        project.dryRunExecute("GetAutoScalingGroups: $names", {
            DescribeAutoScalingGroupsRequest rq = new DescribeAutoScalingGroupsRequest()
            rq.withAutoScalingGroupNames(names)
            
            DescribeAutoScalingGroupsResult rs = getClient().describeAutoScalingGroups(rq)
            autoScalingGroups = rs.getAutoScalingGroups()
            logger.info("Got auto scaling groups: $autoScalingGroups")
        }, {
            logger.debug("DryRun: GetInstances: $names")
        })
        
    }
}
