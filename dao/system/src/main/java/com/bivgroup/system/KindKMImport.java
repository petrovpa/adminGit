package com.bivgroup.system;
// Generated 10.04.2018 17:22:08 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * KindKMImport Generated 10.04.2018 17:22:08 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT
 */
@Entity
@Table(name = "HB_KINDKMIMPORT"
)
public class KindKMImport implements java.io.Serializable {


    private Long id;
    private String name;
    private String sysname;
    private Long rowStatus;

    public KindKMImport() {
    }


    public KindKMImport(Long id) {
        this.id = id;
    }

    public KindKMImport(Long id, String name, String sysname, Long rowStatus) {
        this.id = id;
        this.name = name;
        this.sysname = sysname;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "HB_KINDKMIMPORT_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Column(name = "sysname")
    public String getSysname() {
        return this.sysname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


