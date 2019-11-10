package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author IvanovRoman
 * <p>
 * Класс-результат добавления пользователя в группу
 */
public class GroupUserAddResult {

    @JsonProperty("USERINGROUPID")
    private Long userInGroupId;
    @JsonProperty("PARTID")
    private Long participantId;
    @JsonProperty("CURID")
    private Long groupId;

    public GroupUserAddResult() {
    }

    public GroupUserAddResult(Long userInGroupId, Long participantId, Long groupId) {
        this.userInGroupId = userInGroupId;
        this.participantId = participantId;
        this.groupId = groupId;
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

    /**
     * Получить id пользователя
     *
     * @return - id пользователя
     */
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    /**
     * Получить id группы
     *
     * @return id группы
     */
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupUserAddResult{" +
                "userInGroupId=" + userInGroupId +
                ", participantId=" + participantId +
                ", groupId=" + groupId +
                '}';
    }
}
