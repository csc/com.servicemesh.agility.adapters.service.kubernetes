package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeStatus
{
    private NodeCondition[] conditions;

    private NodeCapacity capacity;

    private DaemonEndpoints daemonEndpoints;

    private NodeAddress[] addresses;

    private NodeInfo nodeInfo;

    public NodeCondition[] getConditions()
    {
        return conditions;
    }

    public void setConditions(NodeCondition[] conditions)
    {
        this.conditions = conditions;
    }

    public NodeCapacity getCapacity()
    {
        return capacity;
    }

    public void setCapacity(NodeCapacity capacity)
    {
        this.capacity = capacity;
    }

    public DaemonEndpoints getDaemonEndpoints()
    {
        return daemonEndpoints;
    }

    public void setDaemonEndpoints(DaemonEndpoints daemonEndpoints)
    {
        this.daemonEndpoints = daemonEndpoints;
    }

    public NodeAddress[] getAddresses()
    {
        return addresses;
    }

    public void setAddresses(NodeAddress[] addresses)
    {
        this.addresses = addresses;
    }

    public NodeInfo getNodeInfo()
    {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo)
    {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public String toString()
    {
        return "NodeStatus [conditions = " + conditions + ", capacity = " + capacity + ", daemonEndpoints = " + daemonEndpoints
                + ", addresses = " + addresses + ", nodeInfo = " + nodeInfo + "]";
    }

}
