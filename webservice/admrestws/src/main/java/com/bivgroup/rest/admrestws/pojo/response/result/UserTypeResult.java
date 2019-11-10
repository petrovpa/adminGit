package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class UserTypeResult {
    private Long id;
    private String systemName;
    private Long code;
    private String fullName;
    private String comment;

    public UserTypeResult() {
    }

    public UserTypeResult(Long id, String systemName, Long code, String fullName, String comment) {
        this.id = id;
        this.systemName = systemName;
        this.code = code;
        this.fullName = fullName;
        this.comment = comment;
    }

    @JsonProperty("REFITEMID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter("SYSNAME")
    public String getSystemName() {
        return systemName;
    }

    @JsonSetter("SHORTVALUE")
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @JsonGetter("NAME")
    public String getFullName() {
        return fullName;
    }

    @JsonSetter("FULLVALUE")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonGetter("COMMENT")
    public String getComment() {
        return comment;
    }

    @JsonSetter("RICOMMENT")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("CODE")
    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserTypeResult{" +
                "id=" + id +
                ", systemName='" + systemName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
