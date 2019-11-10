package com.bivgroup.rest.admrestws.pojo.request.params.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepartmentIdParams {
    private Long departmentId;

    public DepartmentIdParams() {

    }

    public DepartmentIdParams(Long departmentId) {
        this.departmentId = departmentId;
    }

    @JsonProperty("DEPARTMENTID")
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
