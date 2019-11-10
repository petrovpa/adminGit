package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ListUsersInGroupParams {
    private Long userGroupId;

    public ListUsersInGroupParams() {

    }

    public ListUsersInGroupParams(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * Идентификатор группы
     *
     * @return идентификатор группы
     */
    @NotNull(message = "Не передан обязательный параметр: идентификатор группы пользователей")
    @JsonProperty("USERGROUPID")
    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    @Override
    public String toString() {
        return "ListUsersInGroupParams{" +
                "userGroupId=" + userGroupId +
                '}';
    }
}
