package me.gking2224.awsplugin.task

import static org.junit.Assert.*
import me.gking2224.awsplugin.AmazonAWSGradlePlugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class CreateInstanceTaskTest {

    def CreateInstanceTask task
    def Project project
    
    @Before
    def void before() {
        project = ProjectBuilder.builder().build()
        project.ext["aws.access.key"] = "mykey"
        project.ext["aws.secret.key"] = "mySecretKey"
        project.pluginManager.apply AmazonAWSGradlePlugin.NAME
        task = project.task("testTask", type:CreateInstanceTask)
    }
    
    @Test
    public void testNewToken() {
        assertNotNull( task.getUniqueToken() )
    }
    
}
