/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.connection;

import com.servicemesh.core.async.Promise;
import com.servicemesh.io.http.HttpMethod;

/**
 * Performs actions for Amazon Web Services Query APIs.
 */
public interface K8Connection
{
    final static String CONTENT_TYPE = "Content-Type";
    final static String APPLICATION_JSON = "application/json";

    public String getNamespace();

    /**
     * Performs an action via a HTTP GET method.
     *
     * @param responseClass
     *            The class of resource to be retrieved.
     * @return A Promise for the retrieved resource.
     */
    public Promise<byte[]> get(String uri);

    /**
     * Performs an action via a HTTP GET method.
     *
     * @param responseClass
     *            The class of resource to be retrieved.
     * @return A Promise for the retrieved resource.
     */
    public <T> Promise<T> get(String uri, Class<T> responseClass);

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
    public <T> Promise<T> execute(HttpMethod method, String resource, T json, Class<T> responseClass);

}
