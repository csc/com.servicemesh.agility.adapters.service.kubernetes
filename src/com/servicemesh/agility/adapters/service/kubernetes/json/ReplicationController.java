/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicationController
{
    private String apiVersion;

    private ReplicationControllerSpec spec;

    private ReplicationControllerStatus status;

    private String kind;

    private Metadata metadata;

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
    }

    public ReplicationControllerSpec getSpec()
    {
        return spec;
    }

    public void setSpec(ReplicationControllerSpec spec)
    {
        this.spec = spec;
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

    public ReplicationControllerStatus getStatus()
    {
        return status;
    }

    public void setStats(ReplicationControllerStatus status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ReplicationController [apiVersion = " + apiVersion + ", spec = " + spec + ", kind = " + kind + ", metadata = "
                + metadata + "]";
    }
}
