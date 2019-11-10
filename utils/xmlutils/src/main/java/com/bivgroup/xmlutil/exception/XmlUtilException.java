package com.bivgroup.xmlutil.exception;

public class XmlUtilException extends Exception  {
    public XmlUtilException(Exception e) {
        super(e);
    }

    public XmlUtilException(String string, Exception e) {
        super(string, e);
    }

    public XmlUtilException(String string) {
        super(string);
    }
}
