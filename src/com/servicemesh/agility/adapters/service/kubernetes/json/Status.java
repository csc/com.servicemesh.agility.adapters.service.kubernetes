package com.servicemesh.agility.adapters.service.kubernetes.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status
{
    private String message;

    private String apiVersion;

    private StatusDetails details;

    private String reason;

    private String status;

    private Integer code;

    private String kind;

    private Metadata metadata;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
    }

    public StatusDetails getDetails()
    {
        return details;
    }

    public void setDetails(StatusDetails details)
    {
        this.details = details;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public String toString()
    {
        return "Status [message = " + message + ", apiVersion = " + apiVersion + ", details = " + details + ", reason = " + reason
                + ", status = " + status + ", code = " + code + ", kind = " + kind + ", metadata = " + metadata + "]";
    }
}
