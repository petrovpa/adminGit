package com.bivgroup.crm;
// Generated 14.12.2017 18:39:17 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindCountry Generated 14.12.2017 18:39:17 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_COUNTRY"
)
public class KindCountry  implements java.io.Serializable {


     private Long countryId;
     private String phoneCode;
     private String nativeName;
     private Long isNotUse;
     private String flag;
     private String engName;
     private String digitCode;
     private String countryName;
     private String briefName;
     private String alphaCode3;
     private String alphaCode2;
     private Long rowStatus;

    public KindCountry() {
    }

	
    public KindCountry(Long countryId) {
        this.countryId = countryId;
    }
    public KindCountry(Long countryId, String phoneCode, String nativeName, Long isNotUse, String flag, String engName, String digitCode, String countryName, String briefName, String alphaCode3, String alphaCode2, Long rowStatus) {
       this.countryId = countryId;
       this.phoneCode = phoneCode;
       this.nativeName = nativeName;
       this.isNotUse = isNotUse;
       this.flag = flag;
       this.engName = engName;
       this.digitCode = digitCode;
       this.countryName = countryName;
       this.briefName = briefName;
       this.alphaCode3 = alphaCode3;
       this.alphaCode2 = alphaCode2;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_COUNTRY_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="COUNTRYID", nullable=false, insertable=false, updatable=false)
    public Long getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    
    @Column(name="PHONECODE")
    public String getPhoneCode() {
        return this.phoneCode;
    }
    
    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    
    @Column(name="NATIVENAME")
    public String getNativeName() {
        return this.nativeName;
    }
    
    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    
    @Column(name="ISNOTUSE")
    public Long getIsNotUse() {
        return this.isNotUse;
    }
    
    public void setIsNotUse(Long isNotUse) {
        this.isNotUse = isNotUse;
    }

    
    @Column(name="FLAG")
    public String getFlag() {
        return this.flag;
    }
    
    public void setFlag(String flag) {
        this.flag = flag;
    }

    
    @Column(name="ENGNAME")
    public String getEngName() {
        return this.engName;
    }
    
    public void setEngName(String engName) {
        this.engName = engName;
    }

    
    @Column(name="DIGITCODE")
    public String getDigitCode() {
        return this.digitCode;
    }
    
    public void setDigitCode(String digitCode) {
        this.digitCode = digitCode;
    }

    
    @Column(name="COUNTRYNAME")
    public String getCountryName() {
        return this.countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    
    @Column(name="BRIEFNAME")
    public String getBriefName() {
        return this.briefName;
    }
    
    public void setBriefName(String briefName) {
        this.briefName = briefName;
    }

    
    @Column(name="ALPHACODE3")
    public String getAlphaCode3() {
        return this.alphaCode3;
    }
    
    public void setAlphaCode3(String alphaCode3) {
        this.alphaCode3 = alphaCode3;
    }

    
    @Column(name="ALPHACODE2")
    public String getAlphaCode2() {
        return this.alphaCode2;
    }
    
    public void setAlphaCode2(String alphaCode2) {
        this.alphaCode2 = alphaCode2;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


