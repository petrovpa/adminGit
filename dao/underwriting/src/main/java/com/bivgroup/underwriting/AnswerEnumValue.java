package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import com.bivgroup.core.annotation.IsArray;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * AnswerEnumValue Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_ANSWERENUMVALUE")
public class AnswerEnumValue implements java.io.Serializable {

    private Long id;
    private String text;
    private Integer order;
    private AnswerEnumType enumTypeId_EN;
    private Long enumTypeId;
    private Long rowStatus;

    private Boolean isOtherEnabled;

    public AnswerEnumValue() {
    }

    public AnswerEnumValue(Long id) {
        this.id = id;
    }

    public AnswerEnumValue(Long id, String text, Integer order, AnswerEnumType enumTypeId_EN, Long enumTypeId, Long rowStatus) {
        this.id = id;
        this.text = text;
        this.order = order;
        this.enumTypeId_EN = enumTypeId_EN;
        this.enumTypeId = enumTypeId;
        this.rowStatus = rowStatus;
    }

    @Column(name = "ISOTHERENABLED")
    public Boolean getIsOtherEnabled() {
        return isOtherEnabled;
    }

    public void setIsOtherEnabled(Boolean otherEnabled) {
        isOtherEnabled = otherEnabled;
    }


    @SequenceGenerator(name = "generator", sequenceName = "UW_ANSWERENUMVALUE_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "VALUENAME")
    public String getText() {
        return this.text;
    }

    public void setText(String value) {
        this.text = value;
    }


    @Column(name = "ORDERVALUE")
    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ANSWERENUMTYPEID")
    @IsArray
    public AnswerEnumType getEnumTypeId_EN() {
        return this.enumTypeId_EN;
    }

    public void setEnumTypeId_EN(AnswerEnumType enumTypeId_EN) {
        this.enumTypeId_EN = enumTypeId_EN;
    }


    @Column(name = "ANSWERENUMTYPEID", insertable = false, updatable = false)
    public Long getEnumTypeId() {
        return this.enumTypeId;
    }

    public void setEnumTypeId(Long enumTypeId) {
        this.enumTypeId = enumTypeId;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


