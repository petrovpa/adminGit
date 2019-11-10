package com.bivgroup.crm;
// Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * PPersonChild Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="CDM_PPerson_Child"
)
public class PPersonChild  implements java.io.Serializable {


     private Long id;
     private Date dateOfBirth;
     private String name;
     private Long eId;
     private PPerson personId_EN;
     private Long personId;
     private Long rowStatus;

    public PPersonChild() {
    }

	
    public PPersonChild(Long id) {
        this.id = id;
    }
    public PPersonChild(Long id, Date dateOfBirth, String name, Long eId, PPerson personId_EN, Long personId, Long rowStatus) {
       this.id = id;
       this.dateOfBirth = dateOfBirth;
       this.name = name;
       this.eId = eId;
       this.personId_EN = personId_EN;
       this.personId = personId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CDM_PPERSON_CHILD_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="DateOfBirth")
    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PersonID")
    public PPerson getPersonId_EN() {
        return this.personId_EN;
    }
    
    public void setPersonId_EN(PPerson personId_EN) {
        this.personId_EN = personId_EN;
    }

    
    @Column(name="PersonID", insertable=false, updatable=false)
    public Long getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


