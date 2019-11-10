package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class RoleUserRemoveParams {

    private Long userAccountId;
    private Long roleId;

    public RoleUserRemoveParams() {
    }

    public RoleUserRemoveParams(Long userAccountId, Long roleId) {
        this.userAccountId = userAccountId;
        this.roleId = roleId;
    }

    /**
     * Получить уникальный идентификатор владельца(пользователя) аккаунта
     *
     * @return уникальный идентификатор владельца(пользователя) аккаунта
     */
    @NotNull
    @JsonProperty("USERACCOUNTID")
    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    /**
     * Получить уникальный идентификатор роли
     *
     * @return - уникальный идентификатор роли
     */
    @NotNull
    @JsonProperty("ROLEID")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RoleUserRemoveParams{" +
                "userAccountId=" + userAccountId +
                ", roleId=" + roleId +
                '}';
    }
}
