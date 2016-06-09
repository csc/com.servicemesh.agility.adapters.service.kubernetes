/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.connection;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.servicemesh.agility.adapters.service.kubernetes.K8Config;
import com.servicemesh.agility.adapters.service.kubernetes.json.Status;
import com.servicemesh.agility.adapters.service.kubernetes.util.K8AdapterException;
import com.servicemesh.agility.adapters.service.kubernetes.util.K8Util;
import com.servicemesh.agility.adapters.service.kubernetes.util.Resources;
import com.servicemesh.agility.api.Credential;
import com.servicemesh.agility.api.Property;
import com.servicemesh.core.async.CompletablePromise;
import com.servicemesh.core.async.Promise;
import com.servicemesh.core.async.PromiseFactory;
import com.servicemesh.io.http.HttpClientFactory;
import com.servicemesh.io.http.HttpMethod;
import com.servicemesh.io.http.IHttpClient;
import com.servicemesh.io.http.IHttpClientConfigBuilder;
import com.servicemesh.io.http.IHttpHeader;
import com.servicemesh.io.http.IHttpRequest;
import com.servicemesh.io.http.IHttpResponse;
import com.servicemesh.io.proxy.Proxy;

public class K8ConnectionImpl implements K8Connection
{
    private static final Logger logger = Logger.getLogger(K8ConnectionImpl.class);

    private String protocol = "http";
    private String namespace = "default";
    private List<Property> settings;
    private String hostname;
    private Credential credential;
    private Proxy proxy;
    private IHttpClient httpClient;
    private String endpoint;
    private static ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public String getNamespace()
    {
        return namespace;
    }

    K8ConnectionImpl(List<Property> settings, String hostname, Credential cred, Proxy proxy)
    {

        this.settings = settings;
        this.proxy = proxy;

        if ((cred == null) || (!K8Util.isValued(cred.getPublicKey())) || (!K8Util.isValued(cred.getPrivateKey())))
        {
            throw new K8AdapterException(Resources.getString("missingCredential"));
        }
        credential = cred;

        if (hostname == null)
        {
            throw new K8AdapterException(Resources.getString("missingEndpoint"));
        }
        this.hostname = hostname;

        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append("://");
        sb.append(hostname);
        sb.append("/api/");
        sb.append(K8Endpoint.API_VERSION);
        sb.append("/namespaces/");
        sb.append(namespace);
        endpoint = sb.toString();

        IHttpClientConfigBuilder cb = HttpClientFactory.getInstance().getConfigBuilder();
        cb.setConnectionTimeout(K8Config.getConnectionTimeout(settings));
        cb.setRetries(K8Config.getRequestRetries(settings));
        cb.setSocketTimeout(K8Config.getSocketTimeout(settings));
        if (proxy != null)
        {
            cb.setProxy(proxy);
        }
        httpClient = HttpClientFactory.getInstance().getClient(cb.build());
    }

    /**
     * Performs an action via the specified HTTP method.
     *
     * @param method
     *            HTTP method
     * @param params
     *            Query parameters
     * @param responseClass
     *            The class of resource to be retrieved.
     * @return A Promise for the retrieved resource.
     */
    @Override
    public Promise<byte[]> get(String path)
    {
        try
        {
            StringBuilder uri = new StringBuilder();
            uri.append(protocol);
            uri.append("://");
            uri.append(hostname);
            uri.append("/api/");
            uri.append(K8Endpoint.API_VERSION);
            uri.append("/namespaces/");
            uri.append(namespace);
            uri.append(path);

            IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(uri.toString()));
            Promise<IHttpResponse> promise = httpClient.promise(request);
            return promise.flatMap((IHttpResponse response) -> {
                try
                {
                    if (response.getStatus().getStatusCode() != 200)
                    {
                        throw new Exception(response.getStatus().toString());
                    }
                    return Promise.pure(response.getContentAsByteArray());
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    /**
     * Performs an action via the specified HTTP method.
     *
     * @param method
     *            HTTP method
     * @param params
     *            Query parameters
     * @param responseClass
     *            The class of resource to be retrieved.
     * @return A Promise for the retrieved resource.
     */
    @Override
    public <T> Promise<T> get(String path, Class<T> responseClass)
    {
        try
        {
            StringBuilder uri = new StringBuilder();
            uri.append(protocol);
            uri.append("://");
            uri.append(hostname);
            uri.append(path);
            IHttpRequest request = HttpClientFactory.getInstance().createRequest(HttpMethod.GET, new URI(uri.toString()));
            Promise<IHttpResponse> promise = httpClient.promise(request);
            return promise.flatMap((IHttpResponse response) -> {
                try
                {
                    if (response.getStatus().getStatusCode() != 200)
                    {
                        throw new Exception(response.getStatus().toString());
                    }
                    ObjectReader reader = jsonMapper.readerFor(responseClass);
                    T aT = reader.readValue(response.getContentAsStream());
                    return Promise.pure(aT);
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

    /**
     * Performs an action via the specified HTTP method.
     *
     * @param method
     *            HTTP method
     * @param params
     *            Query parameters
     * @param responseClass
     *            The class of resource to be retrieved.
     * @return A Promise for the retrieved resource.
     */
    @Override
    public <T> Promise<T> execute(HttpMethod method, String resource, T body, final Class<T> tClass)
    {
        try
        {
            StringBuilder uri = new StringBuilder();
            uri.append(endpoint);
            uri.append(resource);

            IHttpRequest request = HttpClientFactory.getInstance().createRequest(method, new URI(uri.toString()));

            IHttpHeader contentType =
                    HttpClientFactory.getInstance().createHeader(K8Connection.CONTENT_TYPE, K8Connection.APPLICATION_JSON);

            if (body != null)
            {
                ObjectWriter writer = jsonMapper.writerFor(tClass);
                String json = writer.writeValueAsString(body);
                logger.debug(method.name() + ":" + json);

                request.setContent(json);
                request.setHeader(contentType);
            }
            Promise<IHttpResponse> promise = httpClient.promise(request);
            return promise.flatMap((IHttpResponse response) -> {
                try
                {
                    switch (response.getStatus().getStatusCode())
                    {
                        case 200:
                        case 201:
                        {
                            ObjectReader reader = jsonMapper.readerFor(tClass);
                            String content = response.getContent();
                            logger.debug(content);
                            T value = reader.readValue(content);
                            return Promise.pure(value);
                        }
                        case 404:
                        {
                            CompletablePromise<T> retval = PromiseFactory.create();
                            retval.complete((T) null);
                            return retval;
                        }
                        default:
                        {
                            ObjectReader reader = jsonMapper.readerFor(Status.class);
                            Status status = reader.readValue(response.getContentAsStream());
                            throw new Exception(status.getMessage());
                        }
                    }
                }
                catch (Throwable t)
                {
                    return Promise.pure(t);
                }
            });
        }
        catch (Throwable t)
        {
            return Promise.pure(t);
        }
    }

}
