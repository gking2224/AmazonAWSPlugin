package me.gking2224.awsplugin.task.ecs

import java.lang.reflect.Method

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ecs.model.ContainerDefinition
import com.amazonaws.services.ecs.model.DescribeTaskDefinitionRequest
import com.amazonaws.services.ecs.model.DescribeTaskDefinitionResult
import com.amazonaws.services.ecs.model.NetworkMode
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult
import com.amazonaws.services.ecs.model.TaskDefinition



class RegisterTaskDefinition extends AbstractECSTask {
    
    def family
    def image
    
    def taskDefinitionArn
    def taskDefinitionName

    public RegisterTaskDefinition() {
    }

    @TaskAction
    def registerTaskDefinition() {
        project.dryRunExecute("RegisterTaskDefinition family=$family; image=$image", {
            DescribeTaskDefinitionRequest req = new DescribeTaskDefinitionRequest()
            req.setTaskDefinition(family)
            
            DescribeTaskDefinitionResult tdRes = getClient().describeTaskDefinition(req)
            TaskDefinition td = tdRes.getTaskDefinition()
            
            td.setRevision(td.getRevision() + 1)
            ContainerDefinition cd = td.getContainerDefinitions().get(0)
            logger.info("Setting image to "+image)
            cd.setImage(image)
            
            RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
            request.withFamily(td.getFamily())
            request.withNetworkMode(td.getNetworkMode())
            request.withVolumes(td.getVolumes())
            request.withContainerDefinitions(cd)
            RegisterTaskDefinitionResult res = getClient().registerTaskDefinition(request)
            def newTd = res.getTaskDefinition()
            taskDefinitionArn = newTd.getTaskDefinitionArn()
            taskDefinitionName = "${newTd.getFamily()}:${newTd.getRevision()}"
            
            logger.info("registered task: $taskDefinitionArn")
            
        }, {
            logger.debug("DryRun: RegisterTaskDefinition")
        })
    }
}
