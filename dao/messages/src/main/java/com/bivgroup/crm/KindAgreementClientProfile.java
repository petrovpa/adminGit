package com.bivgroup.crm;
// Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindAgreementClientProfile Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="HB_KINDAGREEMCLIENTPROFILE"
)
public class KindAgreementClientProfile  implements java.io.Serializable {


     private Long id;
     private String sysname;
     private String name;
     private Long rowStatus;

    public KindAgreementClientProfile() {
    }

	
    public KindAgreementClientProfile(Long id) {
        this.id = id;
    }
    public KindAgreementClientProfile(Long id, String sysname, String name, Long rowStatus) {
       this.id = id;
       this.sysname = sysname;
       this.name = name;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDAGREEMCLIENTPROFILE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="name")
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


