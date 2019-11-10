package com.bivgroup.rest.admrestws.pojo.response.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponse {
    private String status;
    private String error;
    private String sessionId;

    public BaseResponse() {
    }

    public BaseResponse(String status) {
        this.status = status;
    }

    public BaseResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("Error")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("SESSIONID")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "BaseResponse{"
                + "status='" + status + '\''
                + ", error='" + error + '\''
                + "sessionId='" + sessionId
                + "\'}";
    }
}
