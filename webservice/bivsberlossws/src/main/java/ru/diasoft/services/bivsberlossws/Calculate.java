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
@XmlRootElement(name = "Calculate")
public class Calculate {
    @XmlElement(name = "prodVerId") private String prodVerId;
    @XmlElement(name = "prodConfId") private String prodConfId;

    @XmlElement(name = "duration") private String duration;
    @XmlElement(name = "travelKind") private String travelKind;
    @XmlElement(name = "territorySysName") private String territorySysName;
    @XmlElement(name = "programSysName") private String programSysName;
    @XmlElement(name = "riskSysNames") private String riskSysNames;
    @XmlElement(name = "isSportEnabled") private String isSportEnabled;
    @XmlElement(name = "insuredCount2") private String insuredCount2;
    @XmlElement(name = "insuredCount60") private String insuredCount60;
    @XmlElement(name = "insuredCount70") private String insuredCount70;
    @XmlElement(name = "currencyId") private String currencyId;

//@XmlElement(name = "action")   private String action;

    public String getProdVerId() {return prodVerId;}
    public void setProdVerId(String prodVerId) {this.prodVerId = prodVerId;}
    public String getProdConfId() {return prodConfId;}
    public void setProdConfId(String prodConfId) {this.prodConfId = prodConfId;}
    
    public String getDuration() {return duration;}
    public void setDuration(String duration) {this.duration = duration;}
    public String getTravelKind() {return travelKind;}
    public void setTravelKind(String travelKind) {this.travelKind = travelKind;}    
    public String getTerritorySysName() {return territorySysName;}
    public void setTerritorySysName(String territorySysName) {this.territorySysName = territorySysName;}    
    public String getProgramSysName() {return programSysName;}
    public void setProgramSysName(String programSysName) {this.programSysName = programSysName;}    
    public String getRiskSysNames() {return riskSysNames;}
    public void setRiskSysNames(String riskSysNames) {this.riskSysNames = riskSysNames;}    
    public String getIsSportEnabled() {return isSportEnabled;}
    public void setIsSportEnabled(String isSportEnabled) {this.isSportEnabled = isSportEnabled;}    
    public String getInsuredCount2() {return insuredCount2;}
    public void setInsuredCount2(String insuredCount2) {this.insuredCount2 = insuredCount2;}    
    public String getInsuredCount60() {return insuredCount60;}
    public void setInsuredCount60(String insuredCount60) {this.insuredCount60 = insuredCount60;}    
    public String getInsuredCount70() {return insuredCount70;}
    public void setInsuredCount70(String insuredCount70) {this.insuredCount70 = insuredCount70;}    
    public String getCurrencyId() {return currencyId;}
    public void setCurrencyId70(String currencyId) {this.currencyId = currencyId;}    
//public String getAction() {return action;} public void setAction(String action) {this.action = action;}
    public Map<String, Object> copyCalculateFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("prodVerId", this.getProdVerId());
        result.put("prodConfId", this.getProdConfId());

        result.put("duration", this.getDuration());
        result.put("travelKind", this.getTravelKind());
        result.put("territorySysName", this.getTerritorySysName());
        result.put("programSysName", this.getProgramSysName());
        result.put("riskSysNames", this.getRiskSysNames());
        result.put("isSportEnabled", this.getIsSportEnabled());
        result.put("insuredCount2", this.getInsuredCount2());
        result.put("insuredCount60", this.getInsuredCount60());
        result.put("insuredCount70", this.getInsuredCount70());
        result.put("currencyId", this.getCurrencyId());
//result.put("action", this.getAction());

        return result;
    }
}
