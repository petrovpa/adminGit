package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:26 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * LegalFormsOfBusiness Generated Sep 6, 2017 5:42:26 PM unknow unknow 
 */
@Entity
@Table(name="HB_LegalFormsOfBusiness"
)
public class LegalFormsOfBusiness  implements java.io.Serializable {


     private Long id;
     private String sysname;
     private Integer personType;
     private String name;
     private Long eId;
     private Long rowStatus;

    public LegalFormsOfBusiness() {
    }

	
    public LegalFormsOfBusiness(Long id) {
        this.id = id;
    }
    public LegalFormsOfBusiness(Long id, String sysname, Integer personType, String name, Long eId, Long rowStatus) {
       this.id = id;
       this.sysname = sysname;
       this.personType = personType;
       this.name = name;
       this.eId = eId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_LEGALFORMSOFBUSINESS_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="Sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="PersonType")
    public Integer getPersonType() {
        return this.personType;
    }
    
    public void setPersonType(Integer personType) {
        this.personType = personType;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


