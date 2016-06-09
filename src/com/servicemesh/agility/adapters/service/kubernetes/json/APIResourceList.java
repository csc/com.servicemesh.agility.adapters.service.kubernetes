/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResourceList
{
    private String name;

    private String namespaced;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNamespaced()
    {
        return namespaced;
    }

    public void setNamespaced(String namespaced)
    {
        this.namespaced = namespaced;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = " + name + ", namespaced = " + namespaced + "]";
    }
}
