package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pod
{
    private String apiVersion;

    private PodSpec spec;

    private PodStatus status;

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

    public PodSpec getSpec()
    {
        return spec;
    }

    public void setSpec(PodSpec spec)
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

    public PodStatus getStatus()
    {
        return status;
    }

    public void setStatus(PodStatus status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "Pod [apiVersion = " + apiVersion + ", spec = " + spec + ", kind = " + kind + ", metadata = " + metadata + "]";
    }
}
