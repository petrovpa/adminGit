package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:24 AM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * RepairShopCar Generated Aug 7, 2017 10:42:24 AM unknow unknow 
 */
@Entity
@Table(name="CH_REPAIRSHOP_CAR"
)
public class RepairShopCar  implements java.io.Serializable {


     private Long id;
     private Integer isNotUse;
     private String name;
     private String name2;
     private String address;
     private String tel;
     private String url;
     private Integer isWarranty;
     private Long markId;
     private Long modelId;
     private Long rowStatus;

    public RepairShopCar() {
    }

	
    public RepairShopCar(Long id) {
        this.id = id;
    }
    public RepairShopCar(Long id, Integer isNotUse, String name, String name2, String address, String tel, String url, Integer isWarranty, Long markId, Long modelId, Long rowStatus) {
       this.id = id;
       this.isNotUse = isNotUse;
       this.name = name;
       this.name2 = name2;
       this.address = address;
       this.tel = tel;
       this.url = url;
       this.isWarranty = isWarranty;
       this.markId = markId;
       this.modelId = modelId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_REPAIRSHOP_CAR_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="isNotUse")
    public Integer getIsNotUse() {
        return this.isNotUse;
    }
    
    public void setIsNotUse(Integer isNotUse) {
        this.isNotUse = isNotUse;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="name2")
    public String getName2() {
        return this.name2;
    }
    
    public void setName2(String name2) {
        this.name2 = name2;
    }

    
    @Column(name="address")
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    
    @Column(name="tel")
    public String getTel() {
        return this.tel;
    }
    
    public void setTel(String tel) {
        this.tel = tel;
    }

    
    @Column(name="url")
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    
    @Column(name="isWarranty")
    public Integer getIsWarranty() {
        return this.isWarranty;
    }
    
    public void setIsWarranty(Integer isWarranty) {
        this.isWarranty = isWarranty;
    }

    
    @Column(name="markId")
    public Long getMarkId() {
        return this.markId;
    }
    
    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    
    @Column(name="modelId")
    public Long getModelId() {
        return this.modelId;
    }
    
    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


