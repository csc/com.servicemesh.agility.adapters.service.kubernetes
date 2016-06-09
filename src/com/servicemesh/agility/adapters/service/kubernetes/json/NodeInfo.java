/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeInfo
{
    private String bootID;

    private String kubeletVersion;

    private String systemUUID;

    private String osImage;

    private String kubeProxyVersion;

    private String kernelVersion;

    private String machineID;

    private String containerRuntimeVersion;

    public String getBootID()
    {
        return bootID;
    }

    public void setBootID(String bootID)
    {
        this.bootID = bootID;
    }

    public String getKubeletVersion()
    {
        return kubeletVersion;
    }

    public void setKubeletVersion(String kubeletVersion)
    {
        this.kubeletVersion = kubeletVersion;
    }

    public String getSystemUUID()
    {
        return systemUUID;
    }

    public void setSystemUUID(String systemUUID)
    {
        this.systemUUID = systemUUID;
    }

    public String getOsImage()
    {
        return osImage;
    }

    public void setOsImage(String osImage)
    {
        this.osImage = osImage;
    }

    public String getKubeProxyVersion()
    {
        return kubeProxyVersion;
    }

    public void setKubeProxyVersion(String kubeProxyVersion)
    {
        this.kubeProxyVersion = kubeProxyVersion;
    }

    public String getKernelVersion()
    {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion)
    {
        this.kernelVersion = kernelVersion;
    }

    public String getMachineID()
    {
        return machineID;
    }

    public void setMachineID(String machineID)
    {
        this.machineID = machineID;
    }

    public String getContainerRuntimeVersion()
    {
        return containerRuntimeVersion;
    }

    public void setContainerRuntimeVersion(String containerRuntimeVersion)
    {
        this.containerRuntimeVersion = containerRuntimeVersion;
    }

    @Override
    public String toString()
    {
        return "NodeInfo [bootID = " + bootID + ", kubeletVersion = " + kubeletVersion + ", systemUUID = " + systemUUID
                + ", osImage = " + osImage + ", kubeProxyVersion = " + kubeProxyVersion + ", kernelVersion = " + kernelVersion
                + ", machineID = " + machineID + ", containerRuntimeVersion = " + containerRuntimeVersion + "]";
    }
}
