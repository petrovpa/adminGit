package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Класс-стуктура по удалению пользователя
 */
public class RoleUserRemoveResult {
    private Long roleAccountId;
    private Long userAccountId;
    private Long roleId;

    public RoleUserRemoveResult() {
    }

    public RoleUserRemoveResult(Long roleAccountId, Long userAccountId, Long roleId) {
        this.roleAccountId = roleAccountId;
        this.userAccountId = userAccountId;
        this.roleId = roleId;
    }

    /**
     * Получить уникальный идентификатор роли аккаунта
     *
     * @return - уникальный идентификатор роли аккаунта
     */
    @NotNull
    @JsonProperty("ROLEACCOUNTID")
    public Long getRoleAccountId() {
        return roleAccountId;
    }

    public void setRoleAccountId(Long roleAccountId) {
        this.roleAccountId = roleAccountId;
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
        return "RoleUserRemoveResult{" +
                "roleAccountId=" + roleAccountId +
                ", userAccountId=" + userAccountId +
                ", roleId=" + roleId +
                '}';
    }
}
