package me.gking2224.awsplugin.task

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

import com.amazonaws.AmazonClientException
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult



class CreateInstanceTask extends AbstractEC2Task {
    
    static final String CLIENT_TOKEN_FILTER = "client-token"
    static final String INSTANCE_IDS = "instance-ids"
    static final String STATE_NAME_FILTER = "instance-state-name"
    static final String STATE_RUNNING = "running"
    
    Logger logger = LoggerFactory.getLogger(this.class)
    
    def count = 1
    def imageId
    def instanceType = "t2.micro"
    def securityGroup
    def timeout = 120
    def interval = 5 // the time between retries, in seconds
    def postInitWaitTime = 30 // in seconds

    public CreateInstanceTask() {
    }

    @TaskAction
    def createInstance() {
        assert imageId != null
        logger.debug "CreateInstanceTask:"
        logger.debug "  count = $count"    
        logger.debug "  imageId=$imageId"
        logger.debug "  instanceType=$instanceType"
        logger.debug "  securityGroup=$securityGroup"
        logger.debug "  timeout=$timeout (secs)"
        logger.debug "  interval=$interval (secs)"
        def token = getUniqueToken()
        logger.debug "Using client token $token"
        def runRequest = new RunInstancesRequest()
        runRequest.setClientToken(token)
        runRequest.setImageId(imageId)
        runRequest.setInstanceType(instanceType)
        runRequest.setMinCount(count)
        runRequest.setMaxCount(count)
        
        if (securityGroup != null) runRequest.setSecurityGroups(Arrays.asList([securityGroup] as String[]))
        
        project.dryRunExecute("CreateInstanceTask ${imageId}", {
            logger.info "Creating AWS EC2 instance with imageId: $imageId"
            RunInstancesResult result = getClient().runInstances(runRequest)
            ext.instanceId = result.reservation.instances[0].instanceId
            logger.info "Created AWS EC2 instance with id: $instanceId"
            def describeRequest = new DescribeInstancesRequest()
            def DescribeInstancesResult describeResult = null
            describeRequest.setInstanceIds([instanceId])
            describeRequest.setFilters(Arrays.asList([
                filter(CLIENT_TOKEN_FILTER, token),
                filter(STATE_NAME_FILTER, STATE_RUNNING)] as Filter[]))
            
            def startTime = System.currentTimeMillis()
            def keepTrying = project.withinTimeout(startTime, timeout*1000)
            describeResult = getClient().describeInstances(describeRequest)
            Object sema4 = new Object()
            synchronized (sema4) {
                while (describeResult.reservations.size() == 0 && keepTrying) {
                    logger.debug "Will try again in $interval seconds"
                    sema4.wait(interval*1000)
                    describeResult = getClient().describeInstances(describeRequest)
                    keepTrying = project.withinTimeout(startTime, timeout*1000)
                }
                if (describeResult.reservations.size() == 0) {
                    throw new RuntimeException("Reached timeout waiting for instance to be ready")
                }
                ext.instance = describeResult.reservations[0].instances[0]
                ext.publicDnsName = instance.publicDnsName
                ext.publicIpAddress = instance.publicIpAddress
                sema4.wait(postInitWaitTime * 1000)
                logger.info "Server ${instanceId} ready [publicDnsName:$publicDnsName, publicIP:$publicIpAddress]"
            }
        }, {
            ext.instanceId = "i-dummy"
            ext.publicDnsName = "dummy.server.com"
            ext.instanceId = "dummyInstanceId"
            ext.publicIpAddress = "0.0.0.0"
            ext.instance = []
            logger.debug("DryRun: Setting publicDns to $publicDnsName")
        })
        
    }
}
