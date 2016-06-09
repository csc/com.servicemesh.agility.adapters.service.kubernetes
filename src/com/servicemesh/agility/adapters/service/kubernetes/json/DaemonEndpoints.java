/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaemonEndpoints
{
    private KubeletEndpoint kubeletEndpoint;

    public KubeletEndpoint getKubeletEndpoint()
    {
        return kubeletEndpoint;
    }

    public void setKubeletEndpoint(KubeletEndpoint kubeletEndpoint)
    {
        this.kubeletEndpoint = kubeletEndpoint;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [kubeletEndpoint = " + kubeletEndpoint + "]";
    }
}
