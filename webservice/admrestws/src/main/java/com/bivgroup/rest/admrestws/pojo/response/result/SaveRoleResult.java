package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс результата сохранения роли
 */
public class SaveRoleResult {
    /**
     * Идентификатор роли
     */
    private Long roleId;

    public SaveRoleResult() {
    }

    public SaveRoleResult(Long roleId) {
        this.roleId = roleId;
    }

    @JsonProperty("ROLEID")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
