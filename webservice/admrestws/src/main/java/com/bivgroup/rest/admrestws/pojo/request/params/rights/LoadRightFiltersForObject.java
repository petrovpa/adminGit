package com.bivgroup.rest.admrestws.pojo.request.params.rights;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Класс описание параметров загрузки фильтров для конкретного права и объекта
 */
public class LoadRightFiltersForObject {
    private Long rightId;
    private Long objectId;
    private String rightOwner;

    public LoadRightFiltersForObject() {
    }

    public LoadRightFiltersForObject(Long rightId, Long objectId, String rightOwner) {
        this.rightId = rightId;
        this.objectId = objectId;
        this.rightOwner = rightOwner;
    }

    @JsonProperty("RIGHTID")
    @NotNull(message = "Не задан обязательный параметр: идентификатор права")
    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    @JsonProperty("OBJECTID")
    @NotNull(message = "Не задан обязательный параметр: идентификатор роли")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @JsonProperty("RIGHTOWNER")
    @NotNull(message = "Не передан обязательный параметр: тип владельца права")
    @Pattern(regexp = "ROLE|USERGROUP|ACCOUNT|DEPARTMENT", message = "Не правильное значение параметра: тип владельца права")
    public String getRightOwner() {
        return rightOwner;
    }

    public void setRightOwner(String rightOwner) {
        this.rightOwner = rightOwner;
    }

    @Override
    public String toString() {
        return "LoadRightFiltersForObject{" +
                "rightId=" + rightId +
                ", objectId=" + objectId +
                ", rightOwner='" + rightOwner + '\'' +
                '}';
    }
}
