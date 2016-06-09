/**
 *              Copyright (c) 2008-2013 ServiceMesh, Incorporated; All Rights Reserved
 *              Copyright (c) 2013-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception triggered for an AWS error response.
 */
public class K8ErrorException extends RuntimeException
{
    private static final long serialVersionUID = 20150630;
    private List<K8Error> _errors;

    public K8ErrorException(String message, K8Error error)
    {
        super(message);
        _errors = new ArrayList<K8Error>();
        _errors.add(error);
    }

    public K8ErrorException(String message, List<K8Error> errors)
    {
        super(message);
        _errors = new ArrayList<K8Error>();
        _errors.addAll(errors);
    }

    public List<K8Error> getErrors()
    {
        return _errors;
    }

    /** Returns a string representation suitable for logging. */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(": ");

        String msg = this.getLocalizedMessage();
        if (msg != null)
            sb.append(msg);

        for (K8Error error : _errors) {
            sb.append(" { ").append(error.toString()).append("}");
        }
        return sb.toString();
    }
}
