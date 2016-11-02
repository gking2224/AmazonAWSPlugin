package me.gking2224.awsplugin.task.ecs

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ecs.model.ListServicesRequest
import com.amazonaws.services.ecs.model.ListServicesResult



class ListServices extends AbstractECSTask {
    
    def clusterName
    
    def serviceArns

    public ListServices() {
    }

    @TaskAction
    def doGetServices() {
        project.dryRunExecute("ListServices: cluster=$clusterName", {
            ListServicesRequest rq = new ListServicesRequest()
            rq.withCluster(clusterName)
            ListServicesResult rs = getClient().listServices(rq)
            serviceArns = rs.getServiceArns()
            logger.info("Got services: $serviceArns")
        }, {
            logger.debug("DryRun: ListServices")
        })
    }
}
