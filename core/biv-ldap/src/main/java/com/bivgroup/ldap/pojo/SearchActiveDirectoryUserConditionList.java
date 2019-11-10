package com.bivgroup.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchActiveDirectoryUserConditionList extends SearchActiveDirectoryUserCondition {
    private boolean searchBlocked;

    public SearchActiveDirectoryUserConditionList() {

    }

    @JsonProperty("ISNEEDBLOCKED")
    public boolean isSearchBlocked() {
        return searchBlocked;
    }

    public void setSearchBlocked(boolean searchBlocked) {
        this.searchBlocked = searchBlocked;
    }
}
