package me.gking2224.awsplugin.task.ecs

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ecs.model.UpdateServiceRequest
import com.amazonaws.services.ecs.model.UpdateServiceResult



class UpdateService extends AbstractECSTask {
    
    def clusterName
    def serviceSuffix = ""
    def taskDefinitionArns

    def updatedServices = [:]
    
    public ListServices() {
    }

    @TaskAction
    def doUpdateService() {
        project.dryRunExecute("UpdateService clusterName=$clusterName; taskDefinitionArns=$taskDefinitionArns; serviceSuffix=$serviceSuffix", {
            
            def suffix = (serviceSuffix != "") ? "-$serviceSuffix" : ""
            taskDefinitionArns.each{ family, arn ->
                UpdateServiceRequest rq = new UpdateServiceRequest()
                rq.withCluster(clusterName)
                def service = "arn:aws:ecs:$region:${project.dockerEcrRegistryId}:service/$family"
                logger.info("using service $service")
                rq.withService(service)
                def taskDefinitionArn = taskDefinitionArns[family]
                rq.withTaskDefinition(taskDefinitionArn)
                UpdateServiceResult rs = getClient().updateService(rq)
                def updatedService = rs.getService()
                updatedServices[family] = updatedService
                logger.debug "Updated service: $updatedService"
            }
        }, {
            logger.debug("DryRun: UpdateService")
        })
    }
}
