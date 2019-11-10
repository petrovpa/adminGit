package com.bivgroup.rest.admrestws.pojo.response;

import com.bivgroup.rest.admrestws.pojo.response.base.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse extends BaseResponse {
    private String error;

    public ErrorResponse() {
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String status, String error) {
        super(status);
        this.error = error;
    }

    @Override
    @JsonProperty("Error")
    public String getError() {
        return error;
    }

    @Override
    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "status='" + this.getStatus() + '\'' +
                ", error='" + error + '\'';
    }
}
