/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSpec
{
    private Map<String, String> selector;

    private ServicePort[] ports;

    private String sessionAffinity;

    private String clusterIP;

    private String type;

    public Map<String, String> getSelector()
    {
        return selector;
    }

    public void setSelector(Map<String, String> selector)
    {
        this.selector = selector;
    }

    public ServicePort[] getPorts()
    {
        return ports;
    }

    public void setPorts(ServicePort[] ports)
    {
        this.ports = ports;
    }

    public String getSessionAffinity()
    {
        return sessionAffinity;
    }

    public void setSessionAffinity(String sessionAffinity)
    {
        this.sessionAffinity = sessionAffinity;
    }

    public String getClusterIP()
    {
        return clusterIP;
    }

    public void setClusterIP(String clusterIP)
    {
        this.clusterIP = clusterIP;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ServiceSpec [selector = " + selector + ", ports = " + ports + ", sessionAffinity = " + sessionAffinity
                + ", clusterIP = " + clusterIP + ", type = " + type + "]";
    }

}
