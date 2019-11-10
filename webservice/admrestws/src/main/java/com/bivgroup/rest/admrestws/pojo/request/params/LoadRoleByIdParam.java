package com.bivgroup.rest.admrestws.pojo.request.params;

import com.bivgroup.rest.admrestws.pojo.request.params.base.ReturnAsHashMap;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Класс параметров запроса загрузки роли по идентификатору
 */
public class LoadRoleByIdParam implements ReturnAsHashMap {
    /**
     * Идентификатор роли
     */
    private Long roleId;

    public LoadRoleByIdParam() {

    }

    public LoadRoleByIdParam(Long roleId) {
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
