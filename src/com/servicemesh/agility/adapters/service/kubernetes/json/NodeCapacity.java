package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeCapacity
{
    private String pods;

    private String cpu;

    private String memory;

    public String getPods()
    {
        return pods;
    }

    public void setPods(String pods)
    {
        this.pods = pods;
    }

    public String getCpu()
    {
        return cpu;
    }

    public void setCpu(String cpu)
    {
        this.cpu = cpu;
    }

    public String getMemory()
    {
        return memory;
    }

    public void setMemory(String memory)
    {
        this.memory = memory;
    }

    @Override
    public String toString()
    {
        return "NodeCapacity [pods = " + pods + ", cpu = " + cpu + ", memory = " + memory + "]";
    }

}
