package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Класс параметров запроса удаления роли по идентификатору
 */
public class DeleteRoleByIdParam {
    /**
     * Идентификатор роли
     */
    private Long roleId;

    public DeleteRoleByIdParam() {

    }

    public DeleteRoleByIdParam(Long roleId) {
        this.roleId = roleId;
    }

    @NotNull(message = "Не передан обязательный параметр: идентификатор роли")
    @JsonProperty("ROLEID")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "LoadRoleByIdParam{" +
                "roleId=" + roleId +
                '}';
    }
}
