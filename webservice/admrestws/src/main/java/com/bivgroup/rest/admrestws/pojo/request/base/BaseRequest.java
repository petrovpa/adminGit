package com.bivgroup.rest.admrestws.pojo.request.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseRequest {
    private String sessionId;

    public BaseRequest() {
    }

    public BaseRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonProperty("sessionid")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;

    }

    @Override
    public String toString() {
        return "sessionId='" + sessionId + '\'';
    }
}
