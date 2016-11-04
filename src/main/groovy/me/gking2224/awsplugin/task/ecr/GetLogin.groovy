package me.gking2224.awsplugin.task.ecr

import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction

import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;

class GetLogin extends AbstractECRTask {
    
    def registryId

    public GetLogin() {
    }

    @TaskAction
    def getLogin() {
        project.dryRunExecute("GetLogin registryId=$registryId", {
            
            def req = new GetAuthorizationTokenRequest()
            req.withRegistryIds(registryId)
            
            def res = getClient().getAuthorizationToken(req)
            
            def authData = res.getAuthorizationData()[0]
            
            String token = new String(Base64.getDecoder().decode(authData.authorizationToken))
            def tz = new StringTokenizer(token, ":")
            
            logger.info("Got login $token")
            project.ext.ecrUsername = tz.nextToken()
            project.ext.ecrPassword = tz.nextToken()
            
            project.ext.ecrRepository = res.getAuthorizationData()[0].proxyEndpoint
        }, {
            logger.debug("DryRun: GetLogin")
        })
        
    }
}
