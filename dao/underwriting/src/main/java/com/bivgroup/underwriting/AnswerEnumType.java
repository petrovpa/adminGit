package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * AnswerEnumType Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_ANSWERENUMTYPE")
public class AnswerEnumType implements java.io.Serializable {

    private Long id;
    private String name;
    private Long rowStatus;

    private Set<AnswerEnumValue> values = new HashSet<>();

    public void addValue(AnswerEnumValue newValue) {
        newValue.setEnumTypeId_EN(this);
        this.values.add(newValue);
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "enumTypeId_EN")
    public Set<AnswerEnumValue> getValues() {
        return values;
    }

    public void setValues(Set<AnswerEnumValue> values) {
        this.values = values;
    }

    public AnswerEnumType() {
    }


    public AnswerEnumType(Long id) {
        this.id = id;
    }

    public AnswerEnumType(Long id, String name, Long rowStatus) {
        this.id = id;
        this.name = name;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_ANSWERENUMTYPE_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


