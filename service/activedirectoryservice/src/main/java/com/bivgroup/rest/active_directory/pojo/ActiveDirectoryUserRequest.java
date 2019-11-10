package com.bivgroup.rest.active_directory.pojo;

import com.bivgroup.ldap.pojo.SearchActiveDirectoryUserConditionList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveDirectoryUserRequest {
    private SearchActiveDirectoryUserConditionList params;
    private String sessionId;

    public ActiveDirectoryUserRequest() {

    }

    public ActiveDirectoryUserRequest(SearchActiveDirectoryUserConditionList params) {
        this.params = params;
    }

    public ActiveDirectoryUserRequest(String sessionId, SearchActiveDirectoryUserConditionList params) {
        this.sessionId = sessionId;
        this.params = params;
    }

    public SearchActiveDirectoryUserConditionList getParams() {
        return params;
    }

    public void setParams(SearchActiveDirectoryUserConditionList params) {
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
