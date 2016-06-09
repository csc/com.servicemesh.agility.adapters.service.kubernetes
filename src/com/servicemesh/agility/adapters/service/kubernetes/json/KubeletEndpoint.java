/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KubeletEndpoint
{
    private String Port;

    public String getPort()
    {
        return Port;
    }

    public void setPort(String Port)
    {
        this.Port = Port;
    }

    @Override
    public String toString()
    {
        return "KubeletEndpoint [Port = " + Port + "]";
    }

}
