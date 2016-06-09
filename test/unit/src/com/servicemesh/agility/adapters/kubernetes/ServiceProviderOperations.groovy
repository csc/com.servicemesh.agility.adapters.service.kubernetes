/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

import com.servicemesh.agility.api.AssetProperty;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.adapters.service.kubernetes.K8Constants;
import com.servicemesh.agility.adapters.service.kubernetes.K8ServiceAdapter;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Connection;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Endpoint;
import com.servicemesh.agility.adapters.service.kubernetes.json.APIVersionList;
import com.servicemesh.agility.adapters.service.kubernetes.operations.K8ServiceProviderOperations;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPingRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.helper.PropertyHelper;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.io.http.HttpMethod;


class K8ServiceProviderOperationsSpec extends spock.lang.Specification
{
    // mock adapter and connection
    K8Connection conn = Mock(K8Connection);
    K8ConnectionFactory factory = Mock(K8ConnectionFactory);
    Reactor reactor = Mock(Reactor);
    K8ServiceAdapter adapter  = [ getReactor: { return reactor; } ] as K8ServiceAdapter; 
    K8ServiceProviderOperations ops = new K8ServiceProviderOperations(factory);
    

    def "ping provider "()
    {
        // setup ping request
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setHostname("hostname");
        ServiceProviderPingRequest request = new ServiceProviderPingRequest();
        request.setProvider(serviceProvider);

        APIVersionList versions = new APIVersionList();
        versions.setVersions(new String[2]);
        versions.getVersions()[0] = "v1";
        versions.getVersions()[1] = "v1.1";

        when:
           factory.getConnection(request.getSettings(),serviceProvider.getHostname(),serviceProvider.getCredentials(),_) >> conn;
           def promise = ops.ping(request);
    
        then:
           1*conn.get("/api", APIVersionList.class) >> Promise.pure(versions);
           def response = promise.get();
           response.getStatus() == Status.COMPLETE;
    }
}

