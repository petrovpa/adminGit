package com.bivgroup.xmlutil.exception;

public class ServiceUnAvailable extends XmlUtilException {
    private final int code;

    public ServiceUnAvailable(int code, Exception e) {
        super(e);
        this.code = code;
    }

    public ServiceUnAvailable(int code, String s, Exception e) {
        super(s, e);
        this.code = code;
    }

    public ServiceUnAvailable(int result, String string) {
        super(string);
        this.code = result;
    }
}
