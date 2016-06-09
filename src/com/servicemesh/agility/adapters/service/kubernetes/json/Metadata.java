/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata
{
    private String generateName;

    private String name;

    private String namespace;

    private String resourceVersion;

    private String uid;

    private String creationTimestamp;

    private String selfLink;

    private Map<String, String> labels;

    public String getCreationTimestamp()
    {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp)
    {
        this.creationTimestamp = creationTimestamp;
    }

    public String getGenerateName()
    {
        return generateName;
    }

    public void setGenerateName(String generateName)
    {
        this.generateName = generateName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public String getResourceVersion()
    {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion)
    {
        this.resourceVersion = resourceVersion;
    }

    public String getSelfLink()
    {
        return selfLink;
    }

    public void setSelfLink(String selfLink)
    {
        this.selfLink = selfLink;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public Map<String, String> getLabels()
    {
        return labels;
    }

    public void setLabels(Map<String, String> labels)
    {
        this.labels = labels;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [resourceVersion = " + resourceVersion + ", selfLink = " + selfLink + "]";
    }
}
