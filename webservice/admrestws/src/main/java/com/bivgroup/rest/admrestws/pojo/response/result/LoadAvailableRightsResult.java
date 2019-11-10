package com.bivgroup.rest.admrestws.pojo.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoadAvailableRightsResult {
    /**
     * Идентификатор роли
     */
    private Long rightId;
    /**
     * Системное имя роли
     */
    private String rightSysname;
    /**
     * Имя роли
     */
    private String rightName;

    private List<MetadataProfileRight> metadata;

    public LoadAvailableRightsResult() {
    }

    public LoadAvailableRightsResult(Long rightId, String rightSysname, String rightName) {
        this.rightId = rightId;
        this.rightSysname = rightSysname;
        this.rightName = rightName;
    }

    @JsonProperty("RIGHTID")
    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    @JsonProperty("RIGHTSYSNAME")
    public String getRightSysname() {
        return rightSysname;
    }

    public void setRightSysname(String rightSysname) {
        this.rightSysname = rightSysname;
    }

    @JsonProperty("RIGHTNAME")
    public String getRightName() {
        return rightName;
    }

    public void setRightName(String rightName) {
        this.rightName = rightName;
    }

    @JsonProperty("METADATA")
    public List<MetadataProfileRight> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataProfileRight> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "LoadAvailableRightsResult{" +
                "rightId=" + rightId +
                ", rightSysname='" + rightSysname + '\'' +
                ", rightName='" + rightName + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
