package com.bivgroup.imports;
// Generated 08.05.2018 18:22:55 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindImportSessionFileExceptionRule Generated 08.05.2018 18:22:55 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="HB_IS_FILEEXRULE"
)
public class KindImportSessionFileExceptionRule  implements java.io.Serializable {


     private Long id;
     private String valueReal;
     private String valueFile;
     private Long rowStatus;

    public KindImportSessionFileExceptionRule() {
    }

	
    public KindImportSessionFileExceptionRule(Long id) {
        this.id = id;
    }
    public KindImportSessionFileExceptionRule(Long id, String valueReal, String valueFile, Long rowStatus) {
       this.id = id;
       this.valueReal = valueReal;
       this.valueFile = valueFile;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_IS_FILEEXRULE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="valueReal")
    public String getValueReal() {
        return this.valueReal;
    }
    
    public void setValueReal(String valueReal) {
        this.valueReal = valueReal;
    }

    
    @Column(name="valueFile")
    public String getValueFile() {
        return this.valueFile;
    }
    
    public void setValueFile(String valueFile) {
        this.valueFile = valueFile;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


