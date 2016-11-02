package me.gking2224.awsplugin.task.ec2

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Reservation


/**
 * http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-instances.html
 * 
 * @author gk
 *
 */
class GetInstances extends AbstractEC2Task {

    static final RUNNING = "running" // per spec

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
        
        project.dryRunExecute("GetInstances: service=$service; env=$env; version=$version", {
            DescribeInstancesRequest rq = new DescribeInstancesRequest()
            def filters =[]
            filters << new Filter("tag:service", Collections.singletonList(service))
            filters << new Filter("tag:env", Collections.singletonList(env))
            filters << new Filter("tag:version", Collections.singletonList(version))
            filters << new Filter("instance-state-code", RUNNING)
            rq.setFilters(filters)
            
            DescribeInstancesResult rs = getClient().describeInstances(rq)
            Collection<Reservation> rsv = rs.getReservations()
            rsv.each {r->
                r.getInstances().each { instances << it }
            }
            logger.info("Got instances: $instances")
        }, {
            logger.debug("DryRun: GetInstances")
        })
        
    }
}
