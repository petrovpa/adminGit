package com.bivgroup.rest.admrestws.pojo.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class LoadFilterMetadataForRightParam {
    private Long rightId;
    private String searchColumn;
    private String searchText;

    public LoadFilterMetadataForRightParam() {
        this.searchColumn = "SYSNAME";
        this.searchText = "";
    }

    @JsonProperty("RIGHTID")
    @NotNull(message = "Не передан обязательный параметр: идентификатор права")
    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    @JsonProperty("SEARCHCOLUMN")
    public String getSearchColumn() {
        return searchColumn;
    }

    public void setSearchColumn(String searchColumn) {
        this.searchColumn = searchColumn;
    }

    @JsonProperty("SEARCHTEXT")
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String toString() {
        return "LoadFilterMetadataForRightParam{" +
                "rightId=" + rightId +
                ", searchColumn='" + searchColumn + '\'' +
                ", searchText='" + searchText + '\'' +
                '}';
    }
}
