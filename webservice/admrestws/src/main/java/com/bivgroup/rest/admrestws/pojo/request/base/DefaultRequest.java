package com.bivgroup.rest.admrestws.pojo.request.base;

import com.bivgroup.request.Request;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DefaultRequest<T> implements Request<T> {
    private T params;
    private String sessionId;

    public DefaultRequest() {

    }

    public DefaultRequest(T params) {
        this.params = params;
    }

    public DefaultRequest(String sessionId, T params) {
        this.sessionId = sessionId;
        this.params = params;
    }

    @Valid
    @NotNull(message = "Не переданы параметры")
    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
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
        return "DefaultRequest{"
                + "params=" + params + ", "
                + "sessionId='" + sessionId + "'}";
    }
}
