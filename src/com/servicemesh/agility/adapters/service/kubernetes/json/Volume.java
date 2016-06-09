package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volume
{
    private String name;

    private Secret secret;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Secret getSecret()
    {
        return secret;
    }

    public void setSecret(Secret secret)
    {
        this.secret = secret;
    }

    @Override
    public String toString()
    {
        return "Volume [name = " + name + ", secret = " + secret + "]";
    }
}
