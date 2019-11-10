/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.diasoft.services.bivsberlossws;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author reson
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Address")
public class Address {

    @XmlElement(name = "participantType")
    private String participantType;
    @XmlElement(name = "countryCode")
    private String countryCode;
    @XmlElement(name = "regionCode")
    private String regionCode;
    @XmlElement(name = "cityCode")
    private String cityCode;
    @XmlElement(name = "streetCode")
    private String streetCode;
    @XmlElement(name = "eCountry")
    private String eCountry;
    @XmlElement(name = "eRegion")
    private String eRegion;
    @XmlElement(name = "eCity")
    private String eCity;
    @XmlElement(name = "eStreetType")
    private String eStreetType;
    @XmlElement(name = "eStreet")
    private String eStreet;
    @XmlElement(name = "eIndex")
    private String eIndex;
    @XmlElement(name = "eHouse")
    private String eHouse;
    @XmlElement(name = "eCorpus")
    private String eCorpus;
    @XmlElement(name = "eBuilding")
    private String eBuilding;
    @XmlElement(name = "eFlat")
    private String eFlat;

    /**
     * @return the participantType
     */
    public String getParticipantType() {
        return participantType;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the regionCode
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * @param regionCode the regionCode to set
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * @param participantType the participantType to set
     */
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    /**
     * @return the cityCode
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * @param cityCode the cityCode to set
     */
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    /**
     * @return the streetCode
     */
    public String getStreetCode() {
        return streetCode;
    }

    /**
     * @param streetCode the streetCode to set
     */
    public void setStreetCode(String streetCode) {
        this.streetCode = streetCode;
    }

    /**
     * @return the eCountry
     */
    public String geteCountry() {
        return eCountry;
    }

    /**
     * @param eCountry the eCountry to set
     */
    public void seteCountry(String eCountry) {
        this.eCountry = eCountry;
    }

    /**
     * @return the eRegion
     */
    public String geteRegion() {
        return eRegion;
    }

    /**
     * @param eRegion the eRegion to set
     */
    public void seteRegion(String eRegion) {
        this.eRegion = eRegion;
    }

    /**
     * @return the eCity
     */
    public String geteCity() {
        return eCity;
    }

    /**
     * @param eCity the eCity to set
     */
    public void seteCity(String eCity) {
        this.eCity = eCity;
    }

    /**
     * @return the eStreetType
     */
    public String geteStreetType() {
        return eStreetType;
    }

    /**
     * @param eStreetType the eStreetType to set
     */
    public void seteStreetType(String eStreetType) {
        this.eStreetType = eStreetType;
    }

    /**
     * @return the eStreet
     */
    public String geteStreet() {
        return eStreet;
    }

    /**
     * @param eStreet the eStreet to set
     */
    public void seteStreet(String eStreet) {
        this.eStreet = eStreet;
    }

    /**
     * @return the eIndex
     */
    public String geteIndex() {
        return eIndex;
    }

    /**
     * @param eIndex the eIndex to set
     */
    public void seteIndex(String eIndex) {
        this.eIndex = eIndex;
    }

    /**
     * @return the eHouse
     */
    public String geteHouse() {
        return eHouse;
    }

    /**
     * @param eHouse the eHouse to set
     */
    public void seteHouse(String eHouse) {
        this.eHouse = eHouse;
    }

    /**
     * @return the eCorpus
     */
    public String geteCorpus() {
        return eCorpus;
    }

    /**
     * @param eCorpus the eCorpus to set
     */
    public void seteCorpus(String eCorpus) {
        this.eCorpus = eCorpus;
    }

    /**
     * @return the eBuilding
     */
    public String geteBuilding() {
        return eBuilding;
    }

    /**
     * @param eBuilding the eBuilding to set
     */
    public void seteBuilding(String eBuilding) {
        this.eBuilding = eBuilding;
    }

    /**
     * @return the eFlat
     */
    public String geteFlat() {
        return eFlat;
    }

    /**
     * @param eFlat the eFlat to set
     */
    public void seteFlat(String eFlat) {
        this.eFlat = eFlat;
    }

    public Map<String, Object> copyAddressFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("PARTICIPANTTYPE", this.getParticipantType());
        result.put("countryCode", this.getCountryCode());
        result.put("regionCode", this.getRegionCode());
        result.put("cityCode", this.getCityCode());
        result.put("streetCode", this.getStreetCode());
        result.put("eCountry", this.geteCountry());
        result.put("eRegion", this.geteRegion());
        result.put("eCity", this.geteCity());
        result.put("eStreetType", this.geteStreetType());
        result.put("eStreet", this.geteStreet());
        result.put("eIndex", this.geteIndex());
        result.put("eHouse", this.geteHouse());
        result.put("eCorpus", this.geteCorpus());
        result.put("eBuilding", this.geteBuilding());
        result.put("eFlat", this.geteFlat());
        return result;
    }
}
