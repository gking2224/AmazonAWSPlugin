package me.gking2224.awsplugin.task.elb

import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.autoscaling.model.DescribeTagsResult
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.Tag
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;;



class GetTargetGroups extends AbstractELBTask {

    def env
    def version
    def service
    
    def targetGroups
    
    def _multipleVersions
    def _multipleEnvs
    
    public GetTargetGroups() {
    }

    @TaskAction
    def doTask() {
        project.dryRunExecute("GetTargetGroups env=$env; version=$version", {
            _multipleVersions = (version == null || version instanceof Collection)
            _multipleEnvs = (env == null || env instanceof Collection)
            
            def _targetGroups = getClient().describeTargetGroups(new DescribeTargetGroupsRequest()).getTargetGroups()
            if (!_multipleVersions && !_multipleEnvs) {
                logger.info("Got targetGroups: $targetGroups")
                targetGroups = _targetGroups
                return
            }
            def _targetGroupTags = [:]
            DescribeTagsRequest tagsRq = new DescribeTagsRequest()
            tagsRq.withResourceArns(_targetGroups.collect{ it.getTargetGroupArn() })
            getClient().describeTags(tagsRq).getTagDescriptions().each {
                _targetGroupTags[it.getResourceArn()] = getTagMap(it.getTags())
            }
            targetGroups = [:]
            if (service != null) _targetGroups = _targetGroups.findAll {
                _targetGroupTags[it.getTargetGroupArn()]["service"] == service
            }
            _targetGroups.each { mapTargetGroup(it, _targetGroupTags[it.getTargetGroupArn()]) }
            
            logger.info("Got targetGroups: $targetGroups")
            
        }, {
            logger.debug("DryRun: GetTargetGroups")
        })
        
    }
    def getTagMap(List<Tag> tags) {
        def m = [:]
        if (tags != null) {
            tags.each {
                m[it.getKey()] = it.getValue()
            }
        }
        return m
    }
    
    def mapTargetGroup(def t, def tags) {
//        println "map group $t using tags $tags"
        if (_multipleEnvs) {
            mapTargetGroupByEnv(t, targetGroups, tags)
        }
        else if (_multipleVersions) {
            mapInstanceByVersion(t, targetGroups, tags)
        }
        else throw new IllegalStateException("should not be here!")
    }
    
    def mapTargetGroupByEnv(def tg, def _targetGroups, def tags) {
        def e = tags["env"]
        if (env != null && (e == null || !env.contains(e))) return
        def ee = (e == null) ? "none" : e
        if (_targetGroups[ee] == null) _targetGroups[ee] = (_multipleVersions) ? [:] : []
        
        if (_multipleVersions) mapInstanceByVersion(tg, _targetGroups[ee], tags)
        else _targetGroups[ee] << tg
    }
    
    def mapInstanceByVersion(def tg, def _targetGroups, def tags) {
        def v = tags["version"]
        if (version != null && (v == null || !version.contains(v))) return
        def vv = (v == null) ? "none" : v
        if (_targetGroups[vv] == null) _targetGroups[vv] = []
        _targetGroups[vv] << tg
    }
}
