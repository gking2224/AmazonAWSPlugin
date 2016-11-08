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
    
    def families = [] as Set
    def image
    
    def taskDefinitionArns = [:]
    def taskDefinitionNames = [:]

    public RegisterTaskDefinition() {
    }

    @TaskAction
    def registerTaskDefinition() {
        project.dryRunExecute("RegisterTaskDefinition families=$families; image=$image", {
            
            families.each { family ->
                
                // get details of the latest task definition for the family
                DescribeTaskDefinitionRequest req = new DescribeTaskDefinitionRequest()
                req.setTaskDefinition(family)
                DescribeTaskDefinitionResult tdRes = getClient().describeTaskDefinition(req)
                
                TaskDefinition td = tdRes.getTaskDefinition()
                
                // reuse existing task definition - increment version
                td.setRevision(td.getRevision() + 1)
                ContainerDefinition cd = td.getContainerDefinitions().get(0)
                logger.info("Setting image for $family revision $revision to $image")
                cd.setImage(image)
                
                // do update
                RegisterTaskDefinitionRequest request = new RegisterTaskDefinitionRequest()
                request.withFamily(td.getFamily())
                request.withNetworkMode(td.getNetworkMode())
                request.withVolumes(td.getVolumes())
                request.withContainerDefinitions(cd)
                
                // get arn/name from result
                RegisterTaskDefinitionResult res = getClient().registerTaskDefinition(request)
                def newTd = res.getTaskDefinition()
                taskDefinitionArns[family] = newTd.getTaskDefinitionArn()
                taskDefinitionNames[family] = "${newTd.getFamily()}:${newTd.getRevision()}"
                
                logger.info("registered task: $taskDefinitionArn")
            }
            
        }, {
            logger.debug("DryRun: RegisterTaskDefinition")
        })
    }
    
    def family(String family) {
        families << family
    }
}
