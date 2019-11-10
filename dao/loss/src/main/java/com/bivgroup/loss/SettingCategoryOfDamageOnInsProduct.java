package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:24 AM unknow unknow 


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * SettingCategoryOfDamageOnInsProduct Generated Aug 7, 2017 10:42:24 AM unknow unknow 
 */
@Entity
@Table(name="PF_PRODUCT_COD"
)
public class SettingCategoryOfDamageOnInsProduct  implements java.io.Serializable {


     private Long id;
     private Long insProductId;
     private Set<CategoryOfDamageOnInsProduct> categories = new HashSet<CategoryOfDamageOnInsProduct>(0);
     private Long rowStatus;

    public SettingCategoryOfDamageOnInsProduct() {
    }

	
    public SettingCategoryOfDamageOnInsProduct(Long id) {
        this.id = id;
    }
    public SettingCategoryOfDamageOnInsProduct(Long id, Long insProductId, Set<CategoryOfDamageOnInsProduct> categories, Long rowStatus) {
       this.id = id;
       this.insProductId = insProductId;
       this.categories = categories;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "PF_PRODUCT_COD_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="insProductId")
    public Long getInsProductId() {
        return this.insProductId;
    }
    
    public void setInsProductId(Long insProductId) {
        this.insProductId = insProductId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="settingId_EN")
    public Set<CategoryOfDamageOnInsProduct> getCategories() {
        return this.categories;
    }
    
    public void setCategories(Set<CategoryOfDamageOnInsProduct> categories) {
        this.categories = categories;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


