package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * UwRiskDetail Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_RISKDETAIL2")
public class UwRiskDetail implements java.io.Serializable {

    private Long id;

    private Long rowStatus;

    private String riskTitle;

    private Double calcClientRate;

    private Double calcReinsRate;

    private Double uwClientRate;

    private Double uwReinsRate;

    private Long uwResultId;
    private UwResult uwResultId_EN;

    public UwRiskDetail() {
    }

    public UwRiskDetail(Long id) {
        this.id = id;
    }

    public UwRiskDetail(Long id, Long rowStatus) {
        this.id = id;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_RISKDETAIL2_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "RISKTITLE")
    public String getRiskTitle() {
        return riskTitle;
    }

    public void setRiskTitle(String riskTitle) {
        this.riskTitle = riskTitle;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

    @Column(name = "CALCCLIENTRATE")
    public Double getCalcClientRate() {
        return calcClientRate;
    }

    public void setCalcClientRate(Double calcClientRate) {
        this.calcClientRate = calcClientRate;
    }

    @Column(name = "CALCREINSRATE")
    public Double getCalcReinsRate() {
        return calcReinsRate;
    }

    public void setCalcReinsRate(Double calcReinsRate) {
        this.calcReinsRate = calcReinsRate;
    }

    @Column(name = "UWCLIENTRATE")
    public Double getUwClientRate() {
        return uwClientRate;
    }

    public void setUwClientRate(Double uwClientRate) {
        this.uwClientRate = uwClientRate;
    }

    @Column(name = "UWREINSRATE")
    public Double getUwReinsRate() {
        return uwReinsRate;
    }

    public void setUwReinsRate(Double uwReinsRate) {
        this.uwReinsRate = uwReinsRate;
    }

    @Column(name = "RESULTID", insertable = false, updatable = false)
    public Long getUwResultId() {
        return uwResultId;
    }

    public void setUwResultId(Long uwResultId) {
        this.uwResultId = uwResultId;
    }

    @ManyToOne
    @JoinColumn(name = "RESULTID")
    public UwResult getUwResultId_EN() {
        return uwResultId_EN;
    }

    public void setUwResultId_EN(UwResult uwResultId_EN) {
        this.uwResultId_EN = uwResultId_EN;
    }
}


