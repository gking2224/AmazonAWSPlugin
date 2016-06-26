package me.gking2224.awsplugin

import java.lang.reflect.Constructor

import org.gradle.api.Project

import com.amazonaws.AmazonServiceException
import com.amazonaws.AmazonWebServiceClient
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.auth.SystemPropertiesCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.RegionUtils
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.identitymanagement.model.GetUserResult
import com.google.common.base.Strings


class AmazonAWSPluginExtension {
    
    private static final String KEY = "awsplugin"
    
    private Project project;
    
    def String profileName = "default";
    
    def String region = Regions.US_EAST_1.getName();

    def String proxyHost;

    def int proxyPort = -1;

    private AWSCredentialsProvider credentialsProvider;
    
    private AmazonEC2Client ec2Client

    public AmazonAWSPluginExtension(Project project) {
        this.project = project;
    }
    
    def getEc2Client() {
        if (ec2Client == null) ec2Client = initClient();
        ec2Client
    }
    
    public AWSCredentialsProvider newCredentialsProvider(String profileName) {
        List<AWSCredentialsProvider> providers = new ArrayList<AWSCredentialsProvider>();
        if(credentialsProvider != null) {
            providers.add(credentialsProvider);
        }
        providers.add(new EnvironmentVariableCredentialsProvider());
        providers.add(new SystemPropertiesCredentialsProvider());
        if (Strings.isNullOrEmpty(profileName) == false) {
//            providers.add(new AwsCliConfigProfileCredentialsProvider(profileName));
            providers.add(new ProfileCredentialsProvider(profileName));
        }
//        providers.add(new AwsCliConfigProfileCredentialsProvider(this.profileName));
        providers.add(new ProfileCredentialsProvider(this.profileName));
        providers.add(new InstanceProfileCredentialsProvider());
        
        return new AWSCredentialsProviderChain(providers.toArray(new AWSCredentialsProvider[providers.size()]));
    }
    
    public <T extends AmazonWebServiceClient>T createClient(Class<T> serviceClass, String profileName) {
        return createClient(serviceClass, profileName, null);
    }

    public <T extends AmazonWebServiceClient>T createClient(Class<T> serviceClass, String profileName, ClientConfiguration config) {
        if (profileName == null) {
            if (this.profileName == null) {
                throw new IllegalStateException("default profileName is null");
            }
            profileName = this.profileName;
        }
        
        AWSCredentialsProvider credentialsProvider = newCredentialsProvider(profileName);
        if (this.proxyHost != null && this.proxyPort > 0) {
            if (config == null) {
                config = new ClientConfiguration();
            }
            config.setProxyHost(this.proxyHost);
            config.setProxyPort(this.proxyPort);
        }
        return _createClient(serviceClass, credentialsProvider, config);
    }
    
    private static <T extends AmazonWebServiceClient>T _createClient(Class<T> serviceClass,
            AWSCredentialsProvider credentials, ClientConfiguration config) {
        Constructor<T> constructor;
        T client;
        try {
            if (credentials == null && config == null) {
                constructor = serviceClass.getConstructor();
                client = constructor.newInstance();
            } else if (credentials == null) {
                constructor = serviceClass.getConstructor(ClientConfiguration.class);
                client = constructor.newInstance(config);
            } else if (config == null) {
                constructor = serviceClass.getConstructor(AWSCredentialsProvider.class);
                client = constructor.newInstance(credentials);
            } else {
                constructor = serviceClass.getConstructor(AWSCredentialsProvider.class, ClientConfiguration.class);
                client = constructor.newInstance(credentials, config);
            }
            
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't instantiate instance of " + serviceClass, e);
        }
    }
    
    public Region getActiveRegion(String clientRegion) {
        if (clientRegion != null) {
            return RegionUtils.getRegion(clientRegion);
        }
        if (this.region == null) {
            throw new IllegalStateException("default region is null");
        }
        return RegionUtils.getRegion(region);
    }
    
    public String getActiveProfileName(String clientProfileName) {
        if (clientProfileName != null) {
            return clientProfileName;
        }
        if (this.profileName == null) {
            throw new IllegalStateException("default profileName is null");
        }
        return profileName;
    }
    
    public String getAccountId() {
        String arn = getUserArn(); // ex. arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/Bob
        return arn.split(":")[4];
    }
    
    public String getUserArn() {
        AmazonIdentityManagement iam = createClient(AmazonIdentityManagementClient.class, profileName);
        try {
            GetUserResult getUserResult = iam.getUser();
            return getUserResult.getUser().getArn();
        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals("AccessDenied") == false) {
                throw e;
            }
            String msg = e.getMessage();
            int arnIdx = msg.indexOf("arn:aws");
            if (arnIdx == -1) {
                throw e;
            }
            int arnSpace = msg.indexOf(" ", arnIdx);
            return msg.substring(arnIdx, arnSpace);
        }
    }

    private AmazonEC2 initClient() {
        AmazonEC2Client client = createClient(com.amazonaws.services.ec2.AmazonEC2Client.class, profileName);
        client.setRegion(getActiveRegion(region));
        return client;
    }
}
