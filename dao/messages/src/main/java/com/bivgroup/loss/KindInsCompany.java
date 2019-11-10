package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * KindInsCompany Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Table(name="HB_KINDINSCOMPANY"
)
public class KindInsCompany  implements java.io.Serializable {


     private Long id;
     private String name;
     private Long rowStatus;

    public KindInsCompany() {
    }

	
    public KindInsCompany(Long id) {
        this.id = id;
    }
    public KindInsCompany(Long id, String name, Long rowStatus) {
       this.id = id;
       this.name = name;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDINSCOMPANY_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
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


