package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class LoadRightFiltersForObjectResult {
    private String sysname;
    private Long rightFilterId;
    private List<FilterValue> filterValues;

    public LoadRightFiltersForObjectResult() {
    }

    public LoadRightFiltersForObjectResult(String sysname, List<FilterValue> filterValues) {
        this.sysname = sysname;
        this.filterValues = filterValues;
    }

    @JsonGetter("FILTERSYSNAME")
    public String getSysname() {
        return sysname;
    }

    @JsonSetter("SYSNAME")
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @JsonProperty("RIGHTFILTERID")
    public Long getRightFilterId() {
        return rightFilterId;
    }

    public void setRightFilterId(Long rightFilterId) {
        this.rightFilterId = rightFilterId;
    }

    @JsonProperty("FILTERVALUES")
    public List<FilterValue> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<FilterValue> filterValues) {
        this.filterValues = filterValues;
    }

    @Override
    public String toString() {
        return "LoadRightFiltersForObjectResult{" +
                "sysname='" + sysname + '\'' +
                ", rightFilterId=" + rightFilterId +
                ", filterValues=" + filterValues +
                '}';
    }
}
