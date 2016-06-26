package me.gking2224.awsplugin.task

import me.gking2224.awsplugin.AmazonAWSGradlePlugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class EC2DescribeInstancesTaskTest {
    
       def project
       def task
       def region = System.getProperty("aws.region")
       
       @Before
       void before() {
           project = ProjectBuilder.builder().build()
           project.pluginManager.apply AmazonAWSGradlePlugin.NAME
           
           // configure plugin
           project.awsplugin {
               region = this.region
           }
           task = project.tasks.ec2DescribeInstances
       }
       @Test
       public void testTaskDefined() {
           def i = System.getProperty("aws.testinstance.instanceid")
           println "system property instance id: $i"
           task.instanceId = i
           println "instanceid: $task.instanceId"
           task.execute()
           assert task.result != null
           def instances =  task.result.reservations.instances.flatten()
           assert instances.size() == 1
           def dns = instances.find().publicDnsName
           assert dns == System.getProperty("aws.testinstance.publicdns")
       }
}
