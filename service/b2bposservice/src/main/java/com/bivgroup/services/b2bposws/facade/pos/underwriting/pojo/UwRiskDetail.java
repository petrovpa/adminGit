package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.bivgroup.core.dictionary.dao.jpa.RowStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UwRiskDetail {

    Long id;

    String riskTitle;
    String reason;

    Double calcClientRate;
    Double calcReinsRate;
    Double uwClientRate;
    Double uwReinsRate;

    Long rowStatus;

    public static UwRiskDetail uwRiskDetail(Map<String, Object> entity) {
        UwRiskDetail uwRiskDetail = new UwRiskDetail();
        uwRiskDetail.setCalcClientRate((Double) entity.get("calcClientRate"));
        uwRiskDetail.setCalcReinsRate((Double) entity.get("calcReinsRate"));
        uwRiskDetail.setUwClientRate((Double) entity.get("uwClientRate"));
        uwRiskDetail.setUwReinsRate((Double) entity.get("uwReinsRate"));
        uwRiskDetail.setRiskTitle((String) entity.get("riskTitle"));
        uwRiskDetail.setId((Long) entity.get("id"));
        return uwRiskDetail;
    }

    public Map<String, Object> toEntity() {
        Map<String, Object> uwRiskDetailId_EN = new HashMap<>();
        uwRiskDetailId_EN.put("id", this.getId());
        uwRiskDetailId_EN.put("calcClientRate", this.getCalcClientRate());
        uwRiskDetailId_EN.put("calcReinsRate", this.getCalcReinsRate());
        uwRiskDetailId_EN.put("uwClientRate", this.getUwClientRate());
        uwRiskDetailId_EN.put("uwReinsRate", this.getUwReinsRate());
        uwRiskDetailId_EN.put("riskTitle", this.getRiskTitle());

        uwRiskDetailId_EN.put("rowStatus", this.getRowStatus());
        if (this.getRowStatus() == null) {
            uwRiskDetailId_EN.put("rowStatus", RowStatus.UNMODIFIED.getId());
        }
        return uwRiskDetailId_EN;
    }

    //<editor-fold desc="get set">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRiskTitle() {
        return riskTitle;
    }

    public void setRiskTitle(String riskTitle) {
        this.riskTitle = riskTitle;
    }

    public Double getCalcClientRate() {
        return calcClientRate;
    }

    public void setCalcClientRate(Double calcClientRate) {
        this.calcClientRate = calcClientRate;
    }

    public Double getCalcReinsRate() {
        return calcReinsRate;
    }

    public void setCalcReinsRate(Double calcReinsRate) {
        this.calcReinsRate = calcReinsRate;
    }

    public Double getUwClientRate() {
        return uwClientRate;
    }

    public void setUwClientRate(Double uwClientRate) {
        this.uwClientRate = uwClientRate;
    }

    public Double getUwReinsRate() {
        return uwReinsRate;
    }

    public void setUwReinsRate(Double uwReinsRate) {
        this.uwReinsRate = uwReinsRate;
    }

    public Long getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }
    //</editor-fold>
}
