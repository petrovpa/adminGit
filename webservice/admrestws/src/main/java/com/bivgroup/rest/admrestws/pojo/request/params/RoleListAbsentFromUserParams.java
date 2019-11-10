package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class RoleListAbsentFromUserParams {
    private Long userAccountId;

    public RoleListAbsentFromUserParams() {

    }

    public RoleListAbsentFromUserParams(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @JsonProperty("USERACCOUNTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор аккаунта пользователя")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Override
    public String toString() {
        return "RoleListAbsentFromUserParams{" +
                "userAccountId=" + userAccountId +
                '}';
    }
}
