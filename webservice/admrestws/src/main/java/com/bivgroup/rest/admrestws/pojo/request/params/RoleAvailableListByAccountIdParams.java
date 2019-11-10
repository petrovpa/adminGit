package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class RoleAvailableListByAccountIdParams {
    private Long userAccountId;
    private String orderBy;

    public RoleAvailableListByAccountIdParams() {
        this.orderBy = "ROLENAME";
    }

    public RoleAvailableListByAccountIdParams(Long userAccountId) {
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

    @JsonProperty("ORDERBY")
    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "RoleAvailableListByAccountIdParams{" +
                "userAccountId=" + userAccountId +
                "orderBy=" + orderBy +
                '}';
    }
}
