/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.operations;

import java.util.Map;

import com.servicemesh.agility.adapters.service.kubernetes.K8Constants;
import com.servicemesh.agility.adapters.service.kubernetes.K8ServiceAdapter;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8Connection;
import com.servicemesh.agility.adapters.service.kubernetes.connection.K8ConnectionFactory;
import com.servicemesh.agility.api.ServiceInstance;
import com.servicemesh.agility.sdk.service.helper.PropertyHelper;
import com.servicemesh.agility.sdk.service.msgs.MethodRequest;
import com.servicemesh.agility.sdk.service.msgs.MethodResponse;
import com.servicemesh.agility.sdk.service.msgs.MethodVariable;
import com.servicemesh.agility.sdk.service.spi.IMethod;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.messaging.Status;

public class K8LogMethod implements IMethod
{
    private K8ConnectionFactory factory;
    private K8ServiceAdapter adapter;

    public K8LogMethod(K8ConnectionFactory factory, K8ServiceAdapter adapter)
    {
        this.factory = factory;
        this.adapter = adapter;
    }

    @Override
    public Promise<MethodResponse> execute(MethodRequest request, Map<String, MethodVariable> params)
    {
        try
        {
            ServiceInstance serviceInstance = request.getServiceInstance();
            switch (serviceInstance.getAssetType().getName())
            {
                case K8Constants.CONTAINER_SERVICE_TYPE:
                    return getContainerConsole(request);

                case K8Constants.K8_POD_SERVICE_TYPE:
                    return getPodConsole(request);

                default:
                    throw new Exception("Invalid service type");
            }
        }
        catch (Throwable t)
        {
            MethodResponse response = new MethodResponse();
            response.setStatus(Status.FAILURE);
            response.setMessage("Invalid service type");
            return Promise.pure(response);
        }
    }

    private Promise<MethodResponse> getContainerConsole(MethodRequest request)
    {
        MethodResponse response = new MethodResponse();
        response.setStatus(Status.FAILURE);
        response.setMessage("Operation not supported");
        return Promise.pure(response);
    }

    private Promise<MethodResponse> getPodConsole(MethodRequest request) throws Exception
    {
        final K8Connection connection = factory.getConnection(request);
        ServiceInstance serviceInstance = request.getServiceInstance();
        String name = PropertyHelper.getString(serviceInstance.getConfigurations(), K8ServiceInstanceOperations.NAME, null);
        if (name == null)
        {
            MethodResponse response = new MethodResponse();
            response.setStatus(Status.FAILURE);
            response.setMessage("Undefined service name");
            return Promise.pure(response);
        }

        Promise<byte[]> promise = connection.get("/pods/" + name + "/log");
        return promise.map((byte[] content) -> {
            MethodResponse response = new MethodResponse();
            MethodVariable var = new MethodVariable();
            var.setName("log");
            PropertyHelper.setString(var.getProperties(), "stdout", new String(content));
            response.getResults().add(var);
            return response;
        });
    }
}
