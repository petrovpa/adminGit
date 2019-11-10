package com.bivgroup.rest.admrestws.pojo.request.base;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.NotNull;

public class Filter {
    private String sysname;
    private String operation;
    private Integer accessMode;
    private Integer anyValue;
    private String values;
    private String keys;

    public Filter() {
        this.accessMode = 1;
        this.operation = "in";
        this.anyValue = 0;
    }

    public Filter(String sysname, String operation, Integer accessMode, Integer anyValue, String values, String keys) {
        this.sysname = sysname;
        this.operation = operation;
        this.accessMode = accessMode;
        this.anyValue = anyValue;
        this.values = values;
        this.keys = keys;
    }

    @JsonProperty("SYSNAME")
    @NotNull(message = "Не передан обязательный параметр: системное имя фильтра")
    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @JsonProperty("OPERATION")
    @NotNull(message = "Не передан обязательный параметр: операция фильтра")
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @JsonProperty("ACCESSMODE")
    public Integer getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(Integer accessMode) {
        this.accessMode = accessMode;
    }

    @JsonProperty("ANYVALUE")
    public Integer getAnyValue() {
        return anyValue;
    }

    public void setAnyValue(Integer anyValue) {
        this.anyValue = anyValue;
    }

    @JsonProperty("VALUES")
    @NotNull(message = "Не передан обязательный параметр: значения фильтра")
    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    @JsonProperty("KEYS")
    @NotNull(message = "Не передан обязательный параметр: ключи фильтра")
    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "sysname='" + sysname + '\'' +
                ", operation='" + operation + '\'' +
                ", accessMode=" + accessMode +
                ", anyValue=" + anyValue +
                ", values='" + values + '\'' +
                ", keys='" + keys + '\'' +
                '}';
    }
}
