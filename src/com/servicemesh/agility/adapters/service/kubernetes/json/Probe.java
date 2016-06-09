/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Probe
{
    private HTTPGetAction httpGet;

    public HTTPGetAction getHttpGet()
    {
        return httpGet;
    }

    public void setHttpGet(HTTPGetAction httpGet)
    {
        this.httpGet = httpGet;
    }
}
