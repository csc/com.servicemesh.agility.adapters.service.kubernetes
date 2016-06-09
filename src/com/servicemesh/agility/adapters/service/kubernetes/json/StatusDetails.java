/**
 *              Copyright (c) 2015-Present Computer Sciences Corporation
 */

package com.servicemesh.agility.adapters.service.kubernetes.json;

public class StatusDetails
{
    private String retryAfterSeconds;

    private String name;

    private StatusCause[] causes;

    private String kind;

    public String getRetryAfterSeconds()
    {
        return retryAfterSeconds;
    }

    public void setRetryAfterSeconds(String retryAfterSeconds)
    {
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public StatusCause[] getCauses()
    {
        return causes;
    }

    public void setCauses(StatusCause[] causes)
    {
        this.causes = causes;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [retryAfterSeconds = " + retryAfterSeconds + ", name = " + name + ", causes = " + causes + ", kind = "
                + kind + "]";
    }

}
