package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class GroupListByAccountIdParams {

    private Long userAccountId;

    public GroupListByAccountIdParams() {

    }

    public GroupListByAccountIdParams(Long userAccountId) {
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
        return "GroupListByAccountIdParams{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}
