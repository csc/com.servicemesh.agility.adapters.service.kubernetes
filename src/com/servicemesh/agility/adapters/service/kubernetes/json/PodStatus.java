/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodStatus
{
    private String startTime;

    private Condition[] conditions;

    private String podIP;

    private String hostIP;

    private String phase;

    private ContainerStatus[] containerStatuses;

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public Condition[] getConditions()
    {
        return conditions;
    }

    public void setConditions(Condition[] conditions)
    {
        this.conditions = conditions;
    }

    public String getPodIP()
    {
        return podIP;
    }

    public void setPodIP(String podIP)
    {
        this.podIP = podIP;
    }

    public String getHostIP()
    {
        return hostIP;
    }

    public void setHostIP(String hostIP)
    {
        this.hostIP = hostIP;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase(String phase)
    {
        this.phase = phase;
    }

    public ContainerStatus[] getContainerStatuses()
    {
        return containerStatuses;
    }

    public void setContainerStatuses(ContainerStatus[] containerStatuses)
    {
        this.containerStatuses = containerStatuses;
    }

    @Override
    public String toString()
    {
        return "PodStatus [startTime = " + startTime + ", conditions = " + conditions + ", podIP = " + podIP + ", hostIP = "
                + hostIP + ", phase = " + phase + ", containerStatuses = " + containerStatuses + "]";
    }
}
