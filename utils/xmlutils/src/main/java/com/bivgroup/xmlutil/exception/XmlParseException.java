package com.bivgroup.xmlutil.exception;

public class XmlParseException extends Exception  {
    public XmlParseException(Exception e) {
        super(e);
    }

    public XmlParseException() {
    }

    public XmlParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
