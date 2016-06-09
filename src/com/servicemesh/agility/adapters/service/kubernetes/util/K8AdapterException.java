/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.util;

/**
 * General exception for adapter related processing.
 */
public class K8AdapterException extends RuntimeException
{
    private static final long serialVersionUID = 20150630;

    public K8AdapterException(String message)
    {
        super(message);
    }

    public K8AdapterException(Throwable cause)
    {
        super(cause);
    }

    public K8AdapterException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
