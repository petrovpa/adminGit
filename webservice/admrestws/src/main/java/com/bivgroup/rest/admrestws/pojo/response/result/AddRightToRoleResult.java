package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddRightToRoleResult {
    private Long rightUserRoleId;

    public AddRightToRoleResult() {
    }

    public AddRightToRoleResult(Long rightUserRoleId) {
        this.rightUserRoleId = rightUserRoleId;
    }

    @JsonProperty("RIGHTUSERROLEID")
    public Long getRightUserRoleId() {
        return rightUserRoleId;
    }

    public void setRightUserRoleId(Long rightUserRoleId) {
        this.rightUserRoleId = rightUserRoleId;
    }

    @Override
    public String toString() {
        return "AddRightToRoleResult{" +
                "rightUserRoleId=" + rightUserRoleId +
                '}';
    }
}
