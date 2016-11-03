package me.gking2224.awsplugin.task.elb

import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.elasticloadbalancingv2.model.DeregisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.RegisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.TargetDescription



class ReRouteInstances extends AbstractELBTask {

    def fromTargetGroupArn
    def toTargetGroupArn
    def instanceIds
    def newVersionTag
    def port = 80
    
    public ReRouteInstances() {
    }

    @TaskAction
    def doTask() {
        project.dryRunExecute("ReRouteInstances", {
            if (instanceIds == null || instanceIds.size() == 0) return
            
            def targets = instanceIds.collect { new TargetDescription().withPort(port).withId(it) }
            
            CreateTagsRequest tagRq = new CreateTagsRequest()
            tagRq.withResources(instanceIds)
            tagRq.withTags(new Tag("version", newVersionTag))
            getClient(AmazonEC2Client.class).createTags(tagRq)
            
            DeregisterTargetsRequest drtRq = new DeregisterTargetsRequest()
            drtRq.withTargetGroupArn(fromTargetGroupArn)
            drtRq.withTargets(targets)
            getClient().deregisterTargets(drtRq)
            
            if (toTargetGroupArn != null) {
                RegisterTargetsRequest rtRq = new RegisterTargetsRequest()
                rtRq.withTargetGroupArn(toTargetGroupArn)
                rtRq.withTargets(targets)
                getClient().registerTargets(rtRq)
            }
            
        }, {
            logger.debug("DryRun: ReRouteInstances")
        })
    }
}
