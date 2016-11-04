package me.gking2224.awsplugin.task.elb

import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.elasticloadbalancingv2.model.DeregisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.RegisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.TargetDescription



class RegisterTargets extends AbstractELBTask {

    def targetGroupArn
    def instanceIds
    def port = 80
    
    public RegisterTargets() {
    }

    @TaskAction
    def doTask() {
        project.dryRunExecute("RegisterTargets fromTargetGroupArn:$targetGroupArn, instanceIds:$instanceIds, port:$port", {
            if (instanceIds == null || instanceIds.size() == 0) return
            
            def targets = instanceIds.collect { new TargetDescription().withPort(port).withId(it) }
            
            RegisterTargetsRequest rtRq = new RegisterTargetsRequest()
            rtRq.withTargetGroupArn(targetGroupArn)
            rtRq.withTargets(targets)
            getClient().registerTargets(rtRq)
            
        }, {
            logger.debug("DryRun: RegisterTargets")
        })
    }
}
