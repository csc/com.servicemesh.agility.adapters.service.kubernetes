/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerStatus
{
    private String name;

    private String image;

    private String imageID;

    private ContainerState state;

    private ContainerState lastState;

    private String ready;

    private String containerID;

    private String restartCount;

    public String getImageID()
    {
        return imageID;
    }

    public void setImageID(String imageID)
    {
        this.imageID = imageID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ContainerState getState()
    {
        return state;
    }

    public void setState(ContainerState state)
    {
        this.state = state;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public ContainerState getLastState()
    {
        return lastState;
    }

    public void setLastState(ContainerState lastState)
    {
        this.lastState = lastState;
    }

    public String getReady()
    {
        return ready;
    }

    public void setReady(String ready)
    {
        this.ready = ready;
    }

    public String getContainerID()
    {
        return containerID;
    }

    public void setContainerID(String containerID)
    {
        this.containerID = containerID;
    }

    public String getRestartCount()
    {
        return restartCount;
    }

    public void setRestartCount(String restartCount)
    {
        this.restartCount = restartCount;
    }

    @Override
    public String toString()
    {
        return "ContainerStatus [imageID = " + imageID + ", name = " + name + ", state = " + state + ", image = " + image
                + ", lastState = " + lastState + ", ready = " + ready + ", containerID = " + containerID + ", restartCount = "
                + restartCount + "]";
    }
}
