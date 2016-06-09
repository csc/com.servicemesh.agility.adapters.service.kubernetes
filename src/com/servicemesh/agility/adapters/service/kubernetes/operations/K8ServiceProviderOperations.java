/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.operations;

import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Connection;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Endpoint;
import com.servicemesh.agility.adapters.service.kubernetes.json.APIVersionList;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderPingRequest;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderResponse;
import com.servicemesh.agility.sdk.service.operations.ServiceProviderOperations;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class K8ServiceProviderOperations extends ServiceProviderOperations
{
    private K8ConnectionFactory factory;

    public K8ServiceProviderOperations(K8ConnectionFactory factory)
    {
        this.factory = factory;
    }

    @Override
    public Promise<ServiceProviderResponse> ping(ServiceProviderPingRequest request)
    {
        try
        {
            ServiceProvider provider = request.getProvider();
            K8Connection connection = factory.getConnection(request.getSettings(), provider.getHostname(),
                    provider.getCredentials(), ServiceAdapter.getProxyConfig(request));
            Promise<APIVersionList> promise = connection.get("/api", APIVersionList.class);
            return promise.map((APIVersionList apiVersions) -> {

                ServiceProviderResponse response = new ServiceProviderResponse();
                for (String version : apiVersions.getVersions())
                {
                    if (version.equalsIgnoreCase(K8Endpoint.API_VERSION))
                    {
                        response.setStatus(Status.COMPLETE);
                        return response;
                    }
                }
                response.setStatus(Status.FAILURE);
                response.setMessage("API Version (" + K8Endpoint.API_VERSION + ") is not supported");
                return response;
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }
}
