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

    public RegisterTaskDefinition() {
    }

    @TaskAction
    def registerTaskDefinition() {
        project.dryRunExecute("RegisterTaskDefinition", {
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
        }, {
            logger.debug("DryRun: RegisterTaskDefinition")
        })
    }
    
//    def configureRequest(Closure c) {
//        c.delegate = request
//        c()
//        
//        
//        request.networkMode(NetworkMode.Bridge)
//        request.task
//    }
//    
//    def methodMissing(String name, args) {
//        Method m = request.getClass().getMethod(name, getTypes(args))
//        m.invoke(request, args)
//    }
//    
//    def propertyMissing(String name, value) {
//        request[name] = value
//    }
    
//    def getTypes(Object... args) {
//        Class[] rv = new Class[args.length]
//        args.eachWithIndex {a, i -> rv[i] = a.getClass() }
//        return rv
//    }
}
