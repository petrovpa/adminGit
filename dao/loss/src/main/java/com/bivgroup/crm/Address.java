package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:18 PM unknow unknow 


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
 * Address Generated Sep 6, 2017 5:42:18 PM unknow unknow 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="SD_Address"
)
public class Address  implements java.io.Serializable {


     private Long id;
     private String streetCode;
     private String street;
     private String villageCode;
     private String village;
     private String cityCode;
     private String city;
     private String districtCode;
     private String district;
     private String regionCode;
     private String region;
     private Integer fillMethod;
     private Long eId;
     private String address2;
     private String address;
     private String postcode;
     private String flat;
     private String house;
     private String isEmptyStreet;
     private KindCountry countryId_EN;
     private Long countryId;
     private KindAddress typeId_EN;
     private Long typeId;
     private Long rowStatus;

    public Address() {
    }

	
    public Address(Long id) {
        this.id = id;
    }
    public Address(Long id, String streetCode, String street, String villageCode, String village, String cityCode, String city, String districtCode, String district, String regionCode, String region, Integer fillMethod, Long eId, String address2, String address, String postcode, String flat, String house, String isEmptyStreet, KindCountry countryId_EN, Long countryId, KindAddress typeId_EN, Long typeId, Long rowStatus) {
       this.id = id;
       this.streetCode = streetCode;
       this.street = street;
       this.villageCode = villageCode;
       this.village = village;
       this.cityCode = cityCode;
       this.city = city;
       this.districtCode = districtCode;
       this.district = district;
       this.regionCode = regionCode;
       this.region = region;
       this.fillMethod = fillMethod;
       this.eId = eId;
       this.address2 = address2;
       this.address = address;
       this.postcode = postcode;
       this.flat = flat;
       this.house = house;
       this.isEmptyStreet = isEmptyStreet;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_ADDRESS_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="StreetCode")
    public String getStreetCode() {
        return this.streetCode;
    }
    
    public void setStreetCode(String streetCode) {
        this.streetCode = streetCode;
    }

    
    @Column(name="Street")
    public String getStreet() {
        return this.street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }

    
    @Column(name="VillageCode")
    public String getVillageCode() {
        return this.villageCode;
    }
    
    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    
    @Column(name="Village")
    public String getVillage() {
        return this.village;
    }
    
    public void setVillage(String village) {
        this.village = village;
    }

    
    @Column(name="CityCode")
    public String getCityCode() {
        return this.cityCode;
    }
    
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    
    @Column(name="City")
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    
    @Column(name="DistrictCode")
    public String getDistrictCode() {
        return this.districtCode;
    }
    
    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    
    @Column(name="District")
    public String getDistrict() {
        return this.district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }

    
    @Column(name="RegionCode")
    public String getRegionCode() {
        return this.regionCode;
    }
    
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    
    @Column(name="Region")
    public String getRegion() {
        return this.region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }

    
    @Column(name="FillMethod")
    public Integer getFillMethod() {
        return this.fillMethod;
    }
    
    public void setFillMethod(Integer fillMethod) {
        this.fillMethod = fillMethod;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

    
    @Column(name="Address2")
    public String getAddress2() {
        return this.address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    
    @Column(name="Address")
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    
    @Column(name="Postcode")
    public String getPostcode() {
        return this.postcode;
    }
    
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    
    @Column(name="Flat")
    public String getFlat() {
        return this.flat;
    }
    
    public void setFlat(String flat) {
        this.flat = flat;
    }

    
    @Column(name="House")
    public String getHouse() {
        return this.house;
    }
    
    public void setHouse(String house) {
        this.house = house;
    }

    
    @Column(name="IsEmptyStreet")
    public String getIsEmptyStreet() {
        return this.isEmptyStreet;
    }
    
    public void setIsEmptyStreet(String isEmptyStreet) {
        this.isEmptyStreet = isEmptyStreet;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="CountryID")
    public KindCountry getCountryId_EN() {
        return this.countryId_EN;
    }
    
    public void setCountryId_EN(KindCountry countryId_EN) {
        this.countryId_EN = countryId_EN;
    }

    
    @Column(name="CountryID", insertable=false, updatable=false)
    public Long getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TypeID")
    public KindAddress getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(KindAddress typeId_EN) {
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


