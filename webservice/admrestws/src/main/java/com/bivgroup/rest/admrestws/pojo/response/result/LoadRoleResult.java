package com.bivgroup.rest.admrestws.pojo.response.result;

import com.bivgroup.rest.admrestws.pojo.common.DoubleDeserializedToString;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Класс результата загрузки роли
 */
public class LoadRoleResult {
    /**
     * Идентификатор роли
     */
    private Long roleId;
    /**
     * Системное имя роли
     */
    private String sysname;
    /**
     * Имя роли
     */
    private String roleName;
    /**
     * Описание роли
     */
    private String description;
    /**
     * Идентификатор проекта
     */
    private Long projectId;
    /**
     * Имя проекта
     */
    private String projectName;
    /**
     * Дата действия роли c
     */
    private String fromDate;
    /**
     * Дата действия роли по
     */
    private String toDate;

    public LoadRoleResult() {
    }

    public LoadRoleResult(Long roleId, String sysname, String roleName, String description, Long projectId,
                          String projectName, String fromDate, String toDate) {
        this.roleId = roleId;
        this.sysname = sysname;
        this.roleName = roleName;
        this.description = description;
        this.projectId = projectId;
        this.projectName = projectName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @JsonProperty("ROLEID")
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @JsonProperty("ROLENAME")
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

    @JsonProperty("PROJECTID")
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("PROJECTNAME")
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @JsonGetter("ROLEDATESTART")
    public String getFromDate() {
        return fromDate;
    }

    @JsonSetter("FROMDATE")
    @JsonDeserialize(using = DoubleDeserializedToString.class)
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("ROLEDATEEND")
    public String getToDate() {
        return toDate;
    }

    @JsonSetter("TODATE")
    @JsonDeserialize(using = DoubleDeserializedToString.class)
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("ROLESYSNAME")
    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @Override
    public String toString() {
        return "LoadRoleResult{" +
                "roleId=" + roleId +
                ", sysname='" + sysname + '\'' +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }
}
