package me.gking2224.awsplugin

import static org.junit.Assert.*
import me.gking2224.awsplugin.task.EC2DescribeInstancesTask

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class AmazonAWSGradlePluginTest {
 
    def project
    
    @Before
    void before() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply AmazonAWSGradlePlugin.NAME
    }
    @Test
    public void testTaskDefined() {
        
        assertTrue(project.tasks.ec2DescribeInstances instanceof EC2DescribeInstancesTask)
    }
    

}
