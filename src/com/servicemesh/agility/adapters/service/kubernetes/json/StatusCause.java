package com.servicemesh.agility.adapters.service.kubernetes.json;

public class StatusCause
{
    private String field;

    private String message;

    private String reason;

    public String getField ()
    {
        return field;
    }

    public void setField (String field)
    {
        this.field = field;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getReason ()
    {
        return reason;
    }

    public void setReason (String reason)
    {
        this.reason = reason;
    }

    @Override
    public String toString()
    {
        return "StatusCause [field = "+field+", message = "+message+", reason = "+reason+"]";
    }
}
