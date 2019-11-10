package com.bivgroup.rest.admrestws.pojo.request.params;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.NotNull;

/**
 * @author Ivanov Roman
 * <p>
 * Класс параметров запроса по добавлению пользователя в группу
 */
public class GroupUserAddParams {

    private Long userId;
    private Long userGroupId;

    public GroupUserAddParams() {
    }

    public GroupUserAddParams(Long userId, Long userGroupId) {
        this.userId = userId;
        this.userGroupId = userGroupId;
    }

    /**
     * Получить id пользователя
     * @return id пользоватея
     */
    @NotNull
    @JsonProperty("USERID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Получить id пользовательской группы
     * @return id пользовательской группы
     */
    @NotNull
    @JsonGetter("USERGROUPID")
    public Long getUserGroupId() {
        return userGroupId;
    }

    @JsonSetter("GROUPID")
    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    @Override
    public String toString() {
        return "GroupUserAddParams{" +
                "userId=" + userId +
                ", userGroupId=" + userGroupId +
                '}';
    }
}
