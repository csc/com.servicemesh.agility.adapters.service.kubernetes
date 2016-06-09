/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePort
{
    private Integer port;

    private String protocol;

    private Integer nodePort;

    private Integer targetPort;

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public Integer getNodePort()
    {
        return nodePort;
    }

    public void setNodePort(Integer nodePort)
    {
        this.nodePort = nodePort;
    }

    public Integer getTargetPort()
    {
        return targetPort;
    }

    public void setTargetPort(Integer targetPort)
    {
        this.targetPort = targetPort;
    }

    @Override
    public String toString()
    {
        return "ServicePort [port = " + port + ", protocol = " + protocol + ", nodePort = " + nodePort + ", targetPort = "
                + targetPort + "]";
    }

}
