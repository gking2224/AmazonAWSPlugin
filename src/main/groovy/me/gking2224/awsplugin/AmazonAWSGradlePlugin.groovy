package me.gking2224.awsplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2Client

import me.gking2224.awsplugin.task.ec2.EC2DescribeInstancesTask;;

class AmazonAWSGradlePlugin implements Plugin<Project> {

    private static final String NAME = "me.gking2224.awsplugin"

	void apply(Project project) {
        
        // declare config extension
		project.extensions.create(AmazonAWSPluginExtension.KEY, AmazonAWSPluginExtension, project)
        
        // define tasks
        project.task(EC2DescribeInstancesTask.NAME, type:EC2DescribeInstancesTask)
	}
}

