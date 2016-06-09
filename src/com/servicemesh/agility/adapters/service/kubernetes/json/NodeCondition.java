/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeCondition
{
    private String message;

    private String reason;

    private String lastHeartbeatTime;

    private String status;

    private String lastTransitionTime;

    private String type;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getLastHeartbeatTime()
    {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(String lastHeartbeatTime)
    {
        this.lastHeartbeatTime = lastHeartbeatTime;
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
        return "ClassPojo [message = " + message + ", reason = " + reason + ", lastHeartbeatTime = " + lastHeartbeatTime
                + ", status = " + status + ", lastTransitionTime = " + lastTransitionTime + ", type = " + type + "]";
    }
}
