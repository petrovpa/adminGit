package com.bivgroup.messages;
// Generated 14.12.2017 18:39:29 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * MessageChannel Generated 14.12.2017 18:39:29 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="HD_MESSAGECHANNEL"
)
public class MessageChannel  implements java.io.Serializable {


     private Long id;
     private String javaClass;
     private String sysName;
     private String name;
     private Long eId;
     private Long rowStatus;

    public MessageChannel() {
    }

	
    public MessageChannel(Long id) {
        this.id = id;
    }
    public MessageChannel(Long id, String javaClass, String sysName, String name, Long eId, Long rowStatus) {
       this.id = id;
       this.javaClass = javaClass;
       this.sysName = sysName;
       this.name = name;
       this.eId = eId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HD_MESSAGECHANNEL_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="javaClass")
    public String getJavaClass() {
        return this.javaClass;
    }
    
    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    
    @Column(name="sysName")
    public String getSysName() {
        return this.sysName;
    }
    
    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="eid")
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


