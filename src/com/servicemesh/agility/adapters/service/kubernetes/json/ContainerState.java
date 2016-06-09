package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerState
{
    private StartedTimestamp running;
    private WaitingReason waiting;

    public StartedTimestamp getRunning()
    {
        return running;
    }

    public void setRunning(StartedTimestamp runnning)
    {
        running = running;
    }

    public WaitingReason getWaiting()
    {
        return waiting;
    }

    public void setWaiting(WaitingReason waiting)
    {
        waiting = waiting;
    }

}
