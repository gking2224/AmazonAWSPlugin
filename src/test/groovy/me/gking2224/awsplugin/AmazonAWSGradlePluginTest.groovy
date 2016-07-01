package me.gking2224.awsplugin

import static org.junit.Assert.*
import me.gking2224.awsplugin.task.EC2DescribeInstancesTask

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class AmazonAWSGradlePluginTest {
 
    def project
    
    @Before
    void before() {
        project = ProjectBuilder.builder().build()
        project.ext["aws.access.key"] = "mykey"
        project.ext["aws.secret.key"] = "mySecretKey"
        project.pluginManager.apply AmazonAWSGradlePlugin.NAME
        project.awsplugin {
            credentials {
                accessKeyId = project["aws.access.key"]
                secretKey = project["aws.secret.key"]
            }
        }
    }
    @Test
    public void testTaskDefined() {
        assertTrue(project.tasks.ec2DescribeInstances != null)
        assertTrue(project.tasks.ec2DescribeInstances instanceof EC2DescribeInstancesTask)
    }
    
    @Test
    public void testExtensionRegistered() {
        Object ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        assertNotNull(ext)
        assertTrue(AmazonAWSPluginExtension.class.isAssignableFrom(ext.class))
    }
    
    @Test
    def void testCredentialsParsed() {
        AmazonAWSPluginExtension ext = project.extensions.getByType(AmazonAWSPluginExtension.class)
        assertNotNull(ext.credentialsProvider)
    }
    
}
