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
@XmlRootElement(name = "Handbook")
public class Handbook {
    @XmlElement(name = "prodVerId") private String prodVerId;
    @XmlElement(name = "prodConfId") private String prodConfId;

//@XmlElement(name = "action")   private String action;

    public String getProdVerId() {return prodVerId;}
    public void setProdVerId(String prodVerId) {this.prodVerId = prodVerId;}
    public String getProdConfId() {return prodConfId;}
    public void setProdConfId(String prodConfId) {this.prodConfId = prodConfId;}
//public String getAction() {return action;} public void setAction(String action) {this.action = action;}
    public Map<String, Object> copyHandbookFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("prodVerId", this.getProdVerId());
        result.put("prodConfId", this.getProdConfId());
//result.put("action", this.getAction());

        return result;
    }
}
