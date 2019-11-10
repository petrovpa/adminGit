package com.bivgroup.services.gosuslygi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс сериализатор JSON to Java-class для idToken
 *
 * @author eremeevas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocUserResponse {
    
    /**
     * Тип документа
     */
    private String type;
    
    /**
     * Подтвержденность документа (VERIFIED/NOT VERIFIED)
     */
    private String vrfStu;
    
    /**
     * Серия документа
     */
    private String series;

    /**
     * Номер документа
     */
    private String number;
    
    /**
     * Дата выдачи документа
     */
    private String issueDate;

    /**
     * Номер подразделения выдавшего документ
     */
    private String issueId;

    /**
     * Кем выдан 
     */
    private String issuedBy;


    public DocUserResponse() {
    }

    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
    
    public String getVrfStu() {
        return vrfStu;
    }

    @JsonProperty("vrfStu")
    public void setVrfStu(String vrfStu) {
        this.vrfStu = vrfStu;
    }
    
    public String getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

    public String getIssueDate() {
        return issueDate;
    }

    @JsonProperty("issueDate")
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueId() {
        return issueId;
    }

    @JsonProperty("issueId")
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    @JsonProperty("issuedBy")
    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

}
