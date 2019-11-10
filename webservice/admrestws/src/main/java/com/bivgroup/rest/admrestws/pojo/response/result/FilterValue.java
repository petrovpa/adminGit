package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilterValue {
    private String value;
    private String vKey;

    public FilterValue() {

    }

    @JsonProperty("VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("VKEY")
    public String getvKey() {
        return vKey;
    }

    public void setvKey(String vKey) {
        this.vKey = vKey;
    }

    @Override
    public String toString() {
        return "FilterValue{" +
                "value='" + value + '\'' +
                ", vKey='" + vKey + '\'' +
                '}';
    }
}
