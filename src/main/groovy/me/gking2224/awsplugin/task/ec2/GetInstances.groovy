package me.gking2224.awsplugin.task.ec2

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Reservation



class GetInstances extends AbstractEC2Task {

    static final String CLIENT_TOKEN_FILTER = "client-token"
    static final String INSTANCE_IDS = "instance-ids"
    static final String STATE_NAME_FILTER = "instance-state-name"
    static final String STATE_RUNNING = "running"
    
    def service
    def env
    def version
    
    def instances = []

    public GetInstances() {
    }

    @TaskAction
    def doTask() {
        
        project.dryRunExecute("GetInstances", {
            DescribeInstancesRequest rq = new DescribeInstancesRequest()
            def filters =[]
            filters << new Filter("tag:service", Collections.singletonList(service))
            filters << new Filter("tag:env", Collections.singletonList(env))
            filters << new Filter("tag:version", Collections.singletonList(version))
            rq.setFilters(filters)
            
            DescribeInstancesResult rs = getClient().describeInstances(rq)
            Collection<Reservation> rsv = rs.getReservations()
            rsv.each {r->
                r.getInstances().each { instances << it }
            }
        }, {
            logger.debug("DryRun: GetInstances")
        })
        
    }
}