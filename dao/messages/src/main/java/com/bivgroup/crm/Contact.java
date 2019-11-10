package com.bivgroup.crm;
// Generated 14.12.2017 18:39:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Contact Generated 14.12.2017 18:39:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="SD_Contact"
)
public class Contact  implements java.io.Serializable {


     private Long id;
     private String value;
     private Long eId;
     private KindContact typeId_EN;
     private Long typeId;
     private Long rowStatus;

    public Contact() {
    }

	
    public Contact(Long id) {
        this.id = id;
    }
    public Contact(Long id, String value, Long eId, KindContact typeId_EN, Long typeId, Long rowStatus) {
       this.id = id;
       this.value = value;
       this.eId = eId;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_CONTACT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="Value")
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TypeID")
    public KindContact getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(KindContact typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="TypeID", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


