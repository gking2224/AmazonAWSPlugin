package me.gking2224.awsplugin.task.ec2

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Filter
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.services.ec2.model.Tag;


/**
 * http://docs.aws.amazon.com/cli/latest/reference/ec2/describe-instances.html
 * 
 * @author gk
 *
 */
class GetInstances extends AbstractEC2Task {

    static final RUNNING = "running" // per spec

    static final String CLIENT_TOKEN_FILTER = "client-token"
    static final String INSTANCE_IDS = "instance-ids"
    static final String STATE_NAME_FILTER = "instance-state-name"
    static final String STATE_RUNNING = "running"
    
    def service
    def env
    def version
    
    def instances
    
    def _multipleVersions
    def _multipleEnvs

    public GetInstances() {
    }

    @TaskAction
    def doTask() {
        
        project.dryRunExecute("GetInstances: service=$service; env=$env; version=$version", {
            DescribeInstancesRequest rq = new DescribeInstancesRequest()
            _multipleVersions = (version == null || version instanceof Collection)
            _multipleEnvs = (env == null || env instanceof Collection)
            instances = (_multipleVersions || _multipleEnvs) ? [:] : []
            
            def e = (_multipleEnvs) ? env : Collections.singletonList(env)
            def v = (_multipleVersions) ? version : Collections.singletonList(version)
            
            def filters =[]
            filters << new Filter("tag:service", Collections.singletonList(service))
            if (e != null) filters << new Filter("tag:env", e)
            if (v != null) filters << new Filter("tag:version", v)
//            filters << new Filter("instance-state-name", Collections.singletonList(RUNNING))
            rq.setFilters(filters)
            
            DescribeInstancesResult rs = getClient().describeInstances(rq)
            Collection<Reservation> rsv = rs.getReservations()
            rsv.each {r-> r.getInstances().each { i -> addInstance(i) } }
            logger.info("Got instances: $instances")
        }, {
            logger.debug("DryRun: GetInstances")
        })
    }
    
    def addInstance(def i) {
        if (_multipleEnvs) {
            mapInstanceByEnv(i, instances)
        }
        else if (_multipleVersions) {
            mapInstanceByVersion(i, instances)
        }
        else instances << i
    }
    
    def mapInstanceByEnv(def i, def _instances) {
        def e = i.getTags().find {Tag t -> t.getKey() == "env"}
        def ee = (e == null) ? "none" : e.getValue()
        if (_instances[ee] == null) _instances[ee] = (_multipleVersions) ? [:] : []
        
        if (_multipleVersions) mapInstanceByVersion(i, _instances[ee])
        else _instances[ee] << i
    }
    
    def mapInstanceByVersion(def i, def _instances) {
        def v = i.getTags().find {Tag t -> t.getKey() == "version"}
        def vv = (v == null) ? "none" : v.getValue()
        if (_instances[vv] == null) _instances[vv] = []
        _instances[vv] << i
    }
}
