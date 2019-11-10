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
@XmlRootElement(name = "Activate")
public class Activate {

    @XmlElement(name = "series")
    private Object series;
    @XmlElement(name = "number")
    private Object number;
    @XmlElement(name = "code")
    private Object code;
    @XmlElement(name = "email")
    private Object email;
    @XmlElement(name = "payDate")
    private Object payDate;
    @XmlElement(name = "isAgree")
    private Object isAgree;
    @XmlElement(name = "contrMap")
    private Object contrMap;
    @XmlElement(name = "activationType")
    private Object activationType;


    public Object getSeries() {
        return series;
    }
    public Object getNumber() {
        return number;
    }
    public Object getCode() {
        return code;
    }
    public Object getEmail() {
        return email;
    }
    public Object getPayDate() {
        return payDate;
    }
    public Object getIsAgree() {
        return isAgree;
    }
    public Object getContrMap() {
         return contrMap;
    }
    public Object getActivationType() {
        return activationType;
    }

    public void setSeries(Object series) {
        this.series = series;
    }
    public void setNumber(Object number) {
        this.number = number;
    }
    public void setCode(Object code) {
        this.code = code;
    }
    public void setEmail(Object email) {
        this.email = email;
    }
    public void setPayDate(Object payDate) {
        this.payDate = payDate;
    }
    public void setIsAgree(Object isAgree) {
        this.isAgree = isAgree;
    }
    public void setContrMap(Object contrMap) {
        this.contrMap = contrMap;
    }
    public void setActivationType(Object activationType) {
        this.activationType = activationType;
    }

    public Map<String, Object> copyActivateFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("SERIES", this.getSeries());
        result.put("NUMBER", this.getNumber());
        result.put("CODE", this.getCode());
        result.put("EMAIL", this.getEmail());
        result.put("PAYDATE", this.getPayDate());
        result.put("ISAGREE", this.getIsAgree());
        result.put("CONTRMAP", this.getContrMap());
        result.put("ACTIVATIONTYPE", this.getActivationType());
        return result;
    }
}
