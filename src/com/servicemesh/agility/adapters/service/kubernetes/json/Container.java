package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Container
{
    private ContainerState state;

    private ContainerStatus status;

    private ResourceRequirements resources;

    private String[] command;

    private String[] args;

    private Env[] env;

    private ContainerPort[] ports;

    private String imagePullPolicy;

    private String name;

    private String image;

    private Probe livenessProbe;

    private Probe readinessProbe;

    private String terminationMessagePath;

    private VolumeMount[] volumeMounts;

    public String[] getArgs()
    {
        return args;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    public String[] getCommand()
    {
        return command;
    }

    public void setCommand(String[] command)
    {
        this.command = command;
    }

    public Env[] getEnv()
    {
        return env;
    }

    public void setEnv(Env[] env)
    {
        this.env = env;
    }

    public VolumeMount[] getVolumeMounts()
    {
        return volumeMounts;
    }

    public void setVolumeMounts(VolumeMount[] volumeMounts)
    {
        this.volumeMounts = volumeMounts;
    }

    public ResourceRequirements getResources()
    {
        return resources;
    }

    public void setResources(ResourceRequirements resources)
    {
        this.resources = resources;
    }

    public ContainerPort[] getPorts()
    {
        return ports;
    }

    public void setPorts(ContainerPort[] ports)
    {
        this.ports = ports;
    }

    public ContainerState getState()
    {
        return state;
    }

    public void setState(ContainerState state)
    {
        this.state = state;
    }

    public ContainerStatus getStatus()
    {
        return status;
    }

    public void setStatus(ContainerStatus status)
    {
        this.status = status;
    }

    public String getImagePullPolicy()
    {
        return imagePullPolicy;
    }

    public void setImagePullPolicy(String imagePullPolicy)
    {
        this.imagePullPolicy = imagePullPolicy;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getTerminationMessagePath()
    {
        return terminationMessagePath;
    }

    public void setTerminationMessagePath(String terminationMessagePath)
    {
        this.terminationMessagePath = terminationMessagePath;
    }

    public Probe getLivenessProbe()
    {
        return livenessProbe;
    }

    public void setLivenessProbe(Probe livenessProbe)
    {
        this.livenessProbe = livenessProbe;
    }

    public Probe getReadinessProbe()
    {
        return readinessProbe;
    }

    public void setReadinessProbe(Probe readinessProbe)
    {
        this.readinessProbe = readinessProbe;
    }

    @Override
    public String toString()
    {
        return "Container [volumeMounts = " + volumeMounts + ", resources = " + resources + ", ports = " + ports
                + ", imagePullPolicy = " + imagePullPolicy + ", name = " + name + ", image = " + image
                + ", terminationMessagePath = " + terminationMessagePath + "]";
    }
}
