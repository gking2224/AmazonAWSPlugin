package me.gking2224.awsplugin.task

import me.gking2224.awsplugin.AmazonAWSGradlePlugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class EC2DescribeInstancesTaskTest {
    
       def project
       def region = System.getProperty("aws.region")
       
       @Before
       void before() {
           project = ProjectBuilder.builder().build()
           project.pluginManager.apply AmazonAWSGradlePlugin.NAME
           
           // configure plugin
           project.awsplugin {
               region = this.region
           }
           assert project.tasks.ec2DescribeInstances
       }
}
