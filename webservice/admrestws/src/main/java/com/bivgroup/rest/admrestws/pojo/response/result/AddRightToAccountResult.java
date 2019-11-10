package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddRightToAccountResult {
    private Long rightAccountId;

    public AddRightToAccountResult() {
    }

    public AddRightToAccountResult(Long rightAccountId) {
        this.rightAccountId = rightAccountId;
    }

    @JsonProperty("RIGHTACCOUNTID")
    public Long getRightAccountId() {
        return rightAccountId;
    }

    public void setRightAccountId(Long rightAccountId) {
        this.rightAccountId = rightAccountId;
    }
}
