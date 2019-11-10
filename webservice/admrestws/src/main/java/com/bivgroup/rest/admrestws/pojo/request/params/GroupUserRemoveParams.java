package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Ivanov Roman
 * <p>
 * Класс-структура, описывающая параметры для запроса удаления пользователя из группы
 */
public class GroupUserRemoveParams {

    private Long userId;
    private Long groupId;

    public GroupUserRemoveParams() {
    }

    public GroupUserRemoveParams(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    /**
     * Получить уникальный номер (id) пользователя
     *
     * @return id пользователя
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
     * Получить уникальный номер группы (id)
     *
     * @return - уникальный номер группы
     */
    @NotNull
    @JsonProperty("GROUPID")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupUserRemoveParams{" +
                ", userId=" + userId +
                ", groupId=" + groupId +
                '}';
    }
}
