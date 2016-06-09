/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

public class Condition
{
    private String lastProbeTime;

    private String status;

    private String lastTransitionTime;

    private String type;

    public String getLastProbeTime()
    {
        return lastProbeTime;
    }

    public void setLastProbeTime(String lastProbeTime)
    {
        this.lastProbeTime = lastProbeTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getLastTransitionTime()
    {
        return lastTransitionTime;
    }

    public void setLastTransitionTime(String lastTransitionTime)
    {
        this.lastTransitionTime = lastTransitionTime;
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
        return "Condition [lastProbeTime = " + lastProbeTime + ", status = " + status + ", lastTransitionTime = "
                + lastTransitionTime + ", type = " + type + "]";
    }
}
