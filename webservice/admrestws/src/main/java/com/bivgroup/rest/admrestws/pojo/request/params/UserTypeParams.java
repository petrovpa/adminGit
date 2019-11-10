package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserTypeParams {
    private String groupName;
    private String referenceName;
    private String shortName;

    public UserTypeParams() {
        this.groupName = "Справочники типов";
        this.referenceName = "Типы пользователей";
    }

    public UserTypeParams(String shortName) {
        this();
        this.shortName = shortName;
    }

    @JsonProperty("GROUPNAME")
    @NotNull(message = "Не передан обязательный параметр имя группы")
    @Pattern(regexp = "Справочники типов", message = "Не верное значение имени группы")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonProperty("REFERENCENAME")
    @NotNull(message = "Не передан обязательный параметр имя ссылки")
    @Pattern(regexp = "Типы пользователей", message = "Не верное значение имени ссылки")
    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @JsonProperty("SHORTVALUE_MASKED")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return "UserTypeParams{" +
                "groupName='" + groupName + '\'' +
                ", referenceName='" + referenceName + '\'' +
                ", code='" + shortName + '\'' +
                '}';
    }
}
