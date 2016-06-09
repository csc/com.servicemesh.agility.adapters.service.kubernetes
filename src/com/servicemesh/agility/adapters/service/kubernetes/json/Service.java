/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service
{
    private ServiceSpec spec;

    private String apiVersion;

    private ServiceStatus status;

    private String kind;

    private Metadata metadata;

    public ServiceSpec getSpec()
    {
        return spec;
    }

    public void setSpec(ServiceSpec spec)
    {
        this.spec = spec;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
    }

    public ServiceStatus getStatus()
    {
        return status;
    }

    public void setStatus(ServiceStatus status)
    {
        this.status = status;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public String toString()
    {
        return "Service [spec = " + spec + ", apiVersion = " + apiVersion + ", status = " + status + ", kind = " + kind
                + ", metadata = " + metadata + "]";
    }

}
