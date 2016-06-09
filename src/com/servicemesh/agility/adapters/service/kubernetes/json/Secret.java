package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Secret
{
    private String secretName;

    public String getSecretName()
    {
        return secretName;
    }

    public void setSecretName(String secretName)
    {
        this.secretName = secretName;
    }

    @Override
    public String toString()
    {
        return "Secret [secretName = " + secretName + "]";
    }
}
