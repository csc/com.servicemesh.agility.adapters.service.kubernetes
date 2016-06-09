package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerPort
{
    private String name;

    private String protocol;

    private String hostPort;

    private String containerPort;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getContainerPort()
    {
        return containerPort;
    }

    public void setContainerPort(String containerPort)
    {
        this.containerPort = containerPort;
    }

    public String getHostPort()
    {
        return hostPort;
    }

    public void setHostPort(String hostPort)
    {
        this.hostPort = hostPort;
    }

    @Override
    public String toString()
    {
        return "Port [protocol = " + protocol + ", containerPort = " + containerPort + "]";
    }
}
