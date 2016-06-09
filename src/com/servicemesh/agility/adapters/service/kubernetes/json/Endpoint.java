package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Endpoint
{
    private APIResourceList[] resources;

    private String groupVersion;

    private String kind;

    public APIResourceList[] getResources()
    {
        return resources;
    }

    public void setResources(APIResourceList[] resources)
    {
        this.resources = resources;
    }

    public String getGroupVersion()
    {
        return groupVersion;
    }

    public void setGroupVersion(String groupVersion)
    {
        this.groupVersion = groupVersion;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [resources = " + resources + ", groupVersion = " + groupVersion + ", kind = " + kind + "]";
    }
}