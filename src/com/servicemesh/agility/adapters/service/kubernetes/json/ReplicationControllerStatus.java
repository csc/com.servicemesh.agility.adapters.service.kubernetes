package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicationControllerStatus
{
    int replicas;
    int observedGeneration;

    public int getReplicas()
    {
        return replicas;
    }

    public void setReplicas(int replicas)
    {
        this.replicas = replicas;
    }

    public int getObservedGeneration()
    {
        return observedGeneration;
    }

    public void setObservedGeneration(int observedGeneration)
    {
        this.observedGeneration = observedGeneration;
    }
}
