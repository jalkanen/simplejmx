package com.ecyrd.simplejmx;

public class SimpleJMXException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public SimpleJMXException(String string, Exception e)
    {
        super(string, e);
    }

}
