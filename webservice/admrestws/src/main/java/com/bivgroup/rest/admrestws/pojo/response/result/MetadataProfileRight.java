package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataProfileRight {
    /**
     * Имя меданных
     */
    private String name;
    /**
     * Систменое имя метаданных
     */
    private String sysname;
    /**
     * Имя компонента для использования на интерфейсе
     */
    private String dataSourceUrl;

    public MetadataProfileRight() {
    }

    public MetadataProfileRight(String name, String sysname, String dataSourceUrl) {
        this.name = name;
        this.sysname = sysname;
        this.dataSourceUrl = dataSourceUrl;
    }

    @JsonProperty("NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("SYSNAME")
    public String getSysname() {
        return sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @JsonProperty("DATASOURCEURL")
    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    @Override
    public String toString() {
        return "MetadataProfileRight{" +
                "name='" + name + '\'' +
                ", sysname='" + sysname + '\'' +
                ", dataSourceUrl='" + dataSourceUrl + '\'' +
                '}';
    }
}
