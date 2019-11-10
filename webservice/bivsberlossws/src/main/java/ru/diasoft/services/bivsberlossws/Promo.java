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
 * @author averichevsm
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Promo")
public class Promo {

    @XmlElement(name = "promoCode")
    private String promoCode;

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Map<String, Object> copyPromoFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("promoCode", this.getPromoCode());

        return result;
    }
}
