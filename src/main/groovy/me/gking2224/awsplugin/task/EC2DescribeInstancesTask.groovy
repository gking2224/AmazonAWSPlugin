package me.gking2224.awsplugin.task

import me.gking2224.awsplugin.AmazonAWSPluginExtension

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.DescribeInstancesRequest


class EC2DescribeInstancesTask extends DefaultTask {

    public static final String NAME = 'ec2DescribeInstances'
    
    String keyName
    String instanceId
    def result
    
	@TaskAction
	void doSomething() {
		
		if (instanceId == null) throw new GradleException("instanceId is required");
		AmazonAWSPluginExtension ext = getProject().getExtensions().getByType(AmazonAWSPluginExtension.class)
		AmazonEC2 ec2 = ext.getEc2Client()
		DescribeInstancesRequest request = new DescribeInstancesRequest()
                .withInstanceIds(instanceId.trim());
		result = ec2.describeInstances(request);
	}
}
