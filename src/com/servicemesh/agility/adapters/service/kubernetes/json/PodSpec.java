package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodSpec
{
    private String serviceAccountName;

    private Integer terminationGracePeriodSeconds;

    private Container[] containers;

    private String nodeName;

    private String serviceAccount;

    private String dnsPolicy;

    private String restartPolicy;

    private Volume[] volumes;

    public String getServiceAccountName()
    {
        return serviceAccountName;
    }

    public void setServiceAccountName(String serviceAccountName)
    {
        this.serviceAccountName = serviceAccountName;
    }

    public Integer getTerminationGracePeriodSeconds()
    {
        return terminationGracePeriodSeconds;
    }

    public void setTerminationGracePeriodSeconds(Integer terminationGracePeriodSeconds)
    {
        this.terminationGracePeriodSeconds = terminationGracePeriodSeconds;
    }

    public Container[] getContainers()
    {
        return containers;
    }

    public void setContainers(Container[] containers)
    {
        this.containers = containers;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public String getServiceAccount()
    {
        return serviceAccount;
    }

    public void setServiceAccount(String serviceAccount)
    {
        this.serviceAccount = serviceAccount;
    }

    public String getDnsPolicy()
    {
        return dnsPolicy;
    }

    public void setDnsPolicy(String dnsPolicy)
    {
        this.dnsPolicy = dnsPolicy;
    }

    public String getRestartPolicy()
    {
        return restartPolicy;
    }

    public void setRestartPolicy(String restartPolicy)
    {
        this.restartPolicy = restartPolicy;
    }

    public Volume[] getVolumes()
    {
        return volumes;
    }

    public void setVolumes(Volume[] volumes)
    {
        this.volumes = volumes;
    }

    @Override
    public String toString()
    {
        return "PodSpec [serviceAccountName = " + serviceAccountName + ", terminationGracePeriodSeconds = "
                + terminationGracePeriodSeconds + ", containers = " + containers + ", nodeName = " + nodeName
                + ", serviceAccount = " + serviceAccount + ", dnsPolicy = " + dnsPolicy + ", restartPolicy = " + restartPolicy
                + ", volumes = " + volumes + "]";
    }
}
