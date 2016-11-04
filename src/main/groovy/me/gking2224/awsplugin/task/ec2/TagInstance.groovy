package me.gking2224.awsplugin.task.ec2

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Tag



class TagInstance extends AbstractEC2Task {

    def instanceId
    def tagKey
    def tagValue

    public TagInstance() {
    }

    @TaskAction
    def createInstance() {
        
        project.dryRunExecute("TagInstance: instanceId: ${instanceId} tagKey: ${tagKey}; tagValue: ${tagValue}", {
            if (instanceId == null) return
            
            def instanceIds = (instanceId instanceof Collection) ? instanceId : [instanceId]
            CreateTagsRequest tagRq = new CreateTagsRequest()
            tagRq.withResources(instanceIds)
            tagRq.withTags(new Tag(tagKey, tagValue))
            
            getClient(AmazonEC2Client.class).createTags(tagRq)
        }, {
            logger.debug("DryRun: TagInstance")
        })
        
    }
}
