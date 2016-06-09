/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceRequirements
{
    private JsonNode limits;
    private JsonNode requests;

    public JsonNode getLimits()
    {
        return limits;
    }

    public void setLimits(JsonNode limits)
    {
        this.limits = limits;
    }

    public JsonNode getRequests()
    {
        return requests;
    }

    public void setRequest(JsonNode request)
    {
        requests = requests;
    }
}
