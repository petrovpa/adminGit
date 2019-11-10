package com.bivgroup.request;

public interface Request<T> {

    public String getSessionId();

    void setSessionId(String sessionId);

    T getParams();

    void setParams(T params);
}
