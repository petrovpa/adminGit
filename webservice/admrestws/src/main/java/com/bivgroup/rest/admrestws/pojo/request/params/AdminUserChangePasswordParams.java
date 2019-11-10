package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminUserChangePasswordParams {
    private String newPassword;
    private Long userAccountId;

    public AdminUserChangePasswordParams() {

    }

    public AdminUserChangePasswordParams(String newPassword, Long userAccountId) {
        this.newPassword = newPassword;
        this.userAccountId = userAccountId;
    }

    @JsonProperty("NEWPASS")
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "AdminUserChangePasswordParams{" +
                "newPassword='" + newPassword + '\'' +
                ", userAccountId=" + userAccountId +
                '}';
    }
}
