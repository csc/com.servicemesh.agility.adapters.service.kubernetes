/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeMount
{
    private String readOnly;

    private String name;

    private String mountPath;

    public String getReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(String readOnly)
    {
        this.readOnly = readOnly;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMountPath()
    {
        return mountPath;
    }

    public void setMountPath(String mountPath)
    {
        this.mountPath = mountPath;
    }

    @Override
    public String toString()
    {
        return "VolumeMount [readOnly = " + readOnly + ", name = " + name + ", mountPath = " + mountPath + "]";
    }
}
