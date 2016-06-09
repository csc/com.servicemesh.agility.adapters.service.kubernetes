package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicationControllerSpec
{
    private int replicas;
    private PodTemplateSpec template;

    public int getReplicas()
    {
        return replicas;
    }

    public void setReplicas(int replicas)
    {
        this.replicas = replicas;
    }

    public PodTemplateSpec getTemplate()
    {
        return template;
    }

    public void setTemplate(PodTemplateSpec template)
    {
        this.template = template;
    }
}
