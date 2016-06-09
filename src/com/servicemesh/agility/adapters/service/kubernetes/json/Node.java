package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node
{
    private NodeSpec spec;

    private NodeStatus status;

    private Metadata metadata;

    public NodeSpec getSpec()
    {
        return spec;
    }

    public void setSpec(NodeSpec spec)
    {
        this.spec = spec;
    }

    public NodeStatus getStatus()
    {
        return status;
    }

    public void setStatus(NodeStatus status)
    {
        this.status = status;
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
        return "Node [spec = " + spec + ", status = " + status + ", metadata = " + metadata + "]";
    }
}
