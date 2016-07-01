package me.gking2224.awsplugin.task

import org.gradle.api.tasks.TaskAction

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult
import com.amazonaws.services.ec2.model.SecurityGroup;



class CreateInstanceTask extends AbstractEC2Task {
    
    static final String TOKEN_FILTER = "client-token"
    static final String STATE_NAME_FILTER = "instance-state-name"
    static final String STATE_RUNNING = "running"
    
    def count = 1
    def imageId
    def instanceType = "t2.micro"
    def securityGroup
    def timeout = 1200000
    def interval = 5000

    public CreateInstanceTask() {
    }

    @TaskAction
    def createInstance() {
        assert imageId != null
        println "CreateInstanceTask:"
        println "  count = $count"    
        println "  imageId=$imageId"
        println "  instanceType=$instanceType"
        println "  securityGroup=$securityGroup"
        println "  timeout=$timeout"
        println "  interval=$interval"
        def token = getUniqueToken()
        def runRequest = new RunInstancesRequest()
        runRequest.setClientToken(token)
        runRequest.setImageId(imageId)
        runRequest.setInstanceType(instanceType)
        runRequest.setMinCount(count)
        runRequest.setMaxCount(count)
        if (securityGroup != null) runRequest.setSecurityGroups(Arrays.asList([securityGroup] as String[]))
        
        def action = {
            RunInstancesResult result = getClient().runInstances(runRequest)
            println result.getReservation().getInstances()
            
            def describeRequest = new DescribeInstancesRequest()
            def DescribeInstancesResult describeResult = null
            describeRequest.setFilters(Arrays.asList([
                filter(TOKEN_FILTER, token),
                filter(STATE_NAME_FILTER, STATE_RUNNING)] as Filter[]))
            
            def startTime = System.currentTimeMillis()
            def keepGoing = true
            describeResult = getClient().describeInstances(describeRequest)
            try {
                Object o = new Object()
                synchronized (o) {
                    while (describeResult.getReservations().size() == 0 && keepGoing) {
                        o.wait(interval)
                        keepGoing = (System.currentTimeMillis() - startTime <= timeout)
                        describeResult = getClient().describeInstances(describeRequest)
                    }
                    if (!keepGoing || describeResult.getReservations().size() == 0) {
                        throw new AmazonClientException("Reached timeout waiting for instance to be ready")
                    }
                    def publicDns = describeResult.getReservations().get(0).getInstances()[0].getPublicDnsName()
                    println "Created server with public dns name $publicDns"
                }
            }
            catch (InterruptedException e) {
                println "CreateInstanceTask interrupted: $e"
            }
        }
        project.dryRunExecute("CreateInstanceTask {$imageId}", action)
        
    }
}
