package com.bivgroup.rest.admrestws.pojo.request.params.rights;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Объект опсания параметров для удаления права у любого объекта
 */
public class RemoveObjectRightsParam {
    private Long rightId;
    private Long objectId;
    private String rightOwner;

    public RemoveObjectRightsParam() {

    }

    public RemoveObjectRightsParam(Long rightId, Long objectId, String rightOwner) {
        this.rightId = rightId;
        this.objectId = objectId;
        this.rightOwner = rightOwner;
    }

    /**
     * Идентификатор права
     *
     * @return идентификатор права
     */
    @JsonProperty("RIGHTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор права, которое требуется удалить у роли")
    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    /**
     * Идентификатор объекта, у которого требуется удалить право
     *
     * @return идентификтаор объекта
     */
    @JsonProperty("OBJECTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор роли, у которой требуется удалить право")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }


    /**
     * Кому назначить право. Так как у нас класс удаления права у роль
     * значит может быть одно доступное значение "ROLE"
     *
     * @return системное имя у кого удалить право
     */
    @JsonProperty("RIGHTOWNER")
    @Pattern(regexp = "ROLE|USERGROUP|ACCOUNT|DEPARTMENT", message = "Не правильное значение параметра: тип владельца права")
    @NotNull(message = "Не передан обязательный параметр: тип владельца права")
    public String getRightOwner() {
        return rightOwner;
    }

    public void setRightOwner(String rightOwner) {
        this.rightOwner = rightOwner;
    }

    @Override
    public String toString() {
        return "RemoveObjectRightsParam{" +
                "rightId=" + rightId +
                ", objectId=" + objectId +
                ", rightOwner='" + rightOwner + '\'' +
                '}';
    }
}
