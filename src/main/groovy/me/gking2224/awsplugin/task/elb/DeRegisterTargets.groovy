package me.gking2224.awsplugin.task.elb

import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.elasticloadbalancingv2.model.DeregisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.RegisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.TargetDescription



class DeRegisterTargets extends AbstractELBTask {

    def targetGroupArn
    def instanceIds
    def port = 80
    
    public DeRegisterTargets() {
    }

    @TaskAction
    def doTask() {
        project.dryRunExecute("DeRegisterTargets fromTargetGroupArn:$targetGroupArn, instanceIds:$instanceIds, port:$port", {
            if (instanceIds == null || instanceIds.size() == 0) {
                logger.debug("Nothing to do")
                return
            }
            
            def targets = instanceIds.collect { new TargetDescription().withPort(port).withId(it) }
            
            DeregisterTargetsRequest drtRq = new DeregisterTargetsRequest()
            drtRq.withTargetGroupArn(targetGroupArn)
            drtRq.withTargets(targets)
            getClient().deregisterTargets(drtRq)
            
        }, {
            logger.debug("DryRun: DeRegisterTargets")
        })
    }
}
