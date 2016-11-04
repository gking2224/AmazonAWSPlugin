package me.gking2224.awsplugin.task.ecs

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ecs.model.UpdateServiceRequest
import com.amazonaws.services.ecs.model.UpdateServiceResult



class UpdateService extends AbstractECSTask {
    
    def clusterName
    def service
    def taskDefinitionArn

    def updatedService
    
    public ListServices() {
    }

    @TaskAction
    def doUpdateService() {
        project.dryRunExecute("UpdateService clusterName=$clusterName; service=$service; taskDefinitionArn=$taskDefinitionArn", {
            UpdateServiceRequest rq = new UpdateServiceRequest()
            rq.withCluster(clusterName)
            rq.withService(service)
            rq.withTaskDefinition(taskDefinitionArn)
            UpdateServiceResult rs = getClient().updateService(rq)
            updatedService = rs.getService()
            logger.debug "Updated service: $updatedService"
        }, {
            logger.debug("DryRun: UpdateService")
        })
    }
}
