package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeSpec
{
    private String externalID;

    public String getExternalID()
    {
        return externalID;
    }

    public void setExternalID(String externalID)
    {
        this.externalID = externalID;
    }

    @Override
    public String toString()
    {
        return "NodeSpec [externalID = " + externalID + "]";
    }
}
