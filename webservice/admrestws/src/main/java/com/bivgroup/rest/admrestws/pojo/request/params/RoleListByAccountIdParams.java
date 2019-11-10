package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class RoleListByAccountIdParams {
    private Long userAccountId;

    public RoleListByAccountIdParams() {

    }

    public RoleListByAccountIdParams(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @NotNull(message = "Не передан обязательный параметр: идентификатор аккаунта пользователя")
    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "RoleListByAccountIdParams{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}
