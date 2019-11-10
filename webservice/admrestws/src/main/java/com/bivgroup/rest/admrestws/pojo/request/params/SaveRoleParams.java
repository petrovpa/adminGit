package com.bivgroup.rest.admrestws.pojo.request.params;

import com.bivgroup.rest.admrestws.pojo.common.StringDateDeserializerToDouble;
import com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNotNull;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;

/**
 * Класс параметров запроса для сохранения роли.
 * Если не указан идентификатор роли, тогда считаем что это создания,
 * для создания обязательны два поля: roleSystemName и roleName.
 */
@NotNullIfDependentNotNull.List({
        @NotNullIfDependentNotNull(message = "Отсутствует системное имя роли", fieldName = "roleSystemName", dependentFieldName = "roleId")
})
public class SaveRoleParams {
    /**
     * Идентификатор роли. Требуется только при обновлении
     */
    private Long roleId;
    /**
     * Системное имя роли. Требуется только при создании.
     */
    private String roleSystemName;
    /**
     * Имя роли. Не может быть пустым ни при обновлении ни при создании.
     */
    private String roleName;
    /**
     * Описание роли
     */
    private String description;
    /**
     * Дата действия роли c
     */
    private Double fromDate;
    /**
     * Дата действия роли по
     */
    private Double toDate;
    /**
     * Идентификатор проекта
     */
    private Long projectId;

    public SaveRoleParams() {
        projectId = 0L;
    }

    public SaveRoleParams(Long roleId, String roleSystemName, String roleName, String description, Double fromDate, Double toDate, Long projectId) {
        this.roleId = roleId;
        this.roleSystemName = roleSystemName;
        this.roleName = roleName;
        this.description = description;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.projectId = projectId;
    }

    @JsonProperty("ROLEID")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @JsonProperty("ROLESYSNAME")
    public String getRoleSystemName() {
        return roleSystemName;
    }

    public void setRoleSystemName(String roleSystemName) {
        this.roleSystemName = roleSystemName;
    }

    @JsonProperty("ROLENAME")
    @NotNull(message = "Отсутствует имя роли")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonProperty("DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter("FROMDATE")
    public Double getFromDate() {
        return fromDate;
    }

    @JsonSetter("ROLEDATESTART")
    @JsonDeserialize(using = StringDateDeserializerToDouble.class)
    public void setFromDate(Double fromDate) {
        this.fromDate = fromDate;
    }

    @JsonGetter("TODATE")
    public Double getToDate() {
        return toDate;
    }

    @JsonSetter("ROLEDATEEND")
    @JsonDeserialize(using = StringDateDeserializerToDouble.class)
    public void setToDate(Double toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("PROJECTID")
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "SaveRoleParams{" +
                "roleId=" + roleId +
                ", roleSystemName='" + roleSystemName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", projectId=" + projectId +
                '}';
    }
}
