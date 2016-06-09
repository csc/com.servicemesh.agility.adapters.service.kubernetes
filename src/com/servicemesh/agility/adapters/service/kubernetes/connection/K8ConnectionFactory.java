/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.connection;

import java.util.List;

import com.servicemesh.agility.api.Credential;
import com.servicemesh.agility.api.Property;
import com.servicemesh.agility.api.ServiceProvider;
import com.servicemesh.agility.sdk.service.msgs.ServiceProviderRequest;
import com.servicemesh.agility.sdk.service.spi.ServiceAdapter;
import com.servicemesh.io.proxy.Proxy;

/**
 * Provides a connection to Amazon Web Services.
 */
public class K8ConnectionFactory
{
    private K8ConnectionFactory()
    {
    }

    private static class Holder
    {
        private static final K8ConnectionFactory _instance = new K8ConnectionFactory();
    }

    /**
     * Gets a connection factory.
     */
    public static K8ConnectionFactory getInstance()
    {
        return Holder._instance;
    }

    /**
     * Gets a K8 connection.
     *
     * @param settings
     *            The configuration settings for the connection. Optional, may be empty or null.
     * @param credential
     *            Must be a credential that contains a public and private key.
     * @param proxy
     *            The proxy to be utilized. Optional, may be null.
     * @return An K8 connection.
     */
    public K8Connection getConnection(ServiceProviderRequest request) throws Exception
    {
        ServiceProvider provider = request.getProvider();
        List<Proxy> proxies = ServiceAdapter.getProxyConfig(request);
        Proxy proxy = null;
        if (proxies != null && (!proxies.isEmpty()))
        {
            proxy = proxies.get(0);
        }
        return new K8ConnectionImpl(request.getSettings(), provider.getHostname(), provider.getCredentials(), proxy);
    }

    /**
     * Gets a K8 connection.
     *
     * @param settings
     *            The configuration settings for the connection. Optional, may be empty or null.
     * @param credential
     *            Must be a credential that contains a public and private key.
     * @param proxy
     *            The proxy to be utilized. Optional, may be null.
     * @return An K8 connection.
     */
    public K8Connection getConnection(List<Property> settings, String hostname, Credential credential, List<Proxy> proxies)
            throws Exception
    {
        Proxy proxy = null;
        if (proxies != null && (!proxies.isEmpty()))
        {
            proxy = proxies.get(0);
        }
        return new K8ConnectionImpl(settings, hostname, credential, proxy);
    }

}
