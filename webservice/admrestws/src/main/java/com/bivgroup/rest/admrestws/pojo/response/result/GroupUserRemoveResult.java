package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author IvanovRoman
 * <p>
 * Класс-результат удаления пользователя из группы
 */
public class GroupUserRemoveResult {
    @JsonProperty("USERINGROUPID")
    private Long userInGroupId;

    public GroupUserRemoveResult() {
    }

    public GroupUserRemoveResult(Long userInGroupId) {
        this.userInGroupId = userInGroupId;
    }

    /**
     * Получить уникальный идентификатор (id) вставленной записи после доаавления пользователя в группу
     *
     * @return - id вставленной записи, связи пользователя и группы
     */
    public Long getUserInGroupId() {
        return userInGroupId;
    }

    public void setUserInGroupId(Long userInGroupId) {
        this.userInGroupId = userInGroupId;
    }

    @Override
    public String toString() {
        return "GroupUserRemoveResult{" +
                "userInGroupId=" + userInGroupId +
                '}';
    }
}
