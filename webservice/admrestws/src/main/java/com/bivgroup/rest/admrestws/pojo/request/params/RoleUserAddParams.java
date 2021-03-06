package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Ivanov Roman
 * <p>
 * Класс параметров запроса по добавлению роли пользователя
 */
public class RoleUserAddParams {

    private Long roleAccountId;
    private Long userAccountId;
    private Long roleId;
    private Float toDate;

    RoleUserAddParams() {
    }

    public RoleUserAddParams(Long roleAccountId, Long userAccountId, Long roleId, Float toDate) {
        this.roleAccountId = roleAccountId;
        this.userAccountId = userAccountId;
        this.roleId = roleId;
        this.toDate = toDate;
    }

    /**
     * Получить уникальный идентификатор роли аккаунта
     *
     * @return - уникальный идентификатор роли аккаунта
     */
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

    /**
     * Получить текущую дату (дату, когда назначили)
     *
     * @return теуущая дата (дата, когда назначили)
     */
    @JsonProperty("TODATE")
    public Float getToDate() {
        return toDate;
    }

    public void setToDate(Float toDate) {
        this.toDate = toDate;
    }


    @Override
    public String toString() {
        return "RoleUserAddParams{" +
                "roleAccountId=" + roleAccountId +
                ", userAccountId=" + userAccountId +
                ", roleId=" + roleId +
                ", toDate=" + toDate +
                '}';
    }
}
