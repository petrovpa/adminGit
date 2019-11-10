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
@XmlRootElement(name = "Payment")
public class Payment {

@XmlElement(name = "contrId")   private String contrId;
@XmlElement(name = "guid")   private String guid;
@XmlElement(name = "hash")   private String hash;
@XmlElement(name = "useB2B")   private String useB2B;
@XmlElement(name = "isMobile")   private String isMobile;
@XmlElement(name = "protocol")   private String protocol;
@XmlElement(name = "host")   private String host;
@XmlElement(name = "port")   private String port;
@XmlElement(name = "urlPay")   private String urlPay;
@XmlElement(name = "smsCode")   private String smsCode;
@XmlElement(name = "action")   private String action;
@XmlElement(name = "sendMod")   private String sendMod;
@XmlElement(name = "type")   private String type; // Тип платежа для того, что бы различать способ оплаты
@XmlElement(name = "phone")   private String phone; // Номер телефона, к которому привязан сбербанк - онлайн для подтв по СМС
@XmlElement(name = "email")   private String email; // Почта для чека от Яндекс Кассы

public String getContrId() {return contrId;} public void setContrId(String contrId) {this.contrId = contrId;}
public String getGuid() {return guid;} public void setGuid(String guid) {this.guid = guid;}
public String getHash() {return hash;} public void setHash(String hash) {this.hash = hash;}
public String getUseB2B() {return useB2B;} public void setUseB2B(String useB2B) {this.useB2B = useB2B;}
public String getIsMobile() {return isMobile;} public void setIsMobile(String isMobile) {this.isMobile = isMobile;}
public String getProtocol() {return protocol;} public void setProtocol(String protocol) {this.protocol = protocol;}
public String getHost() {return host;} public void setHost(String host) {this.host = host;}
public String getPort() {return port;} public void setPort(String port) {this.port = port;}
public String getUrlPay() {return urlPay;} public void setUrlPay(String urlPay) {this.urlPay = urlPay;}
public String getSmsCode() {return smsCode;} public void setSmsCode(String smsCode) {this.smsCode = smsCode;}
public String getAction() {return action;} public void setAction(String action) {this.action = action;}
public String getSendMod() {return sendMod;} public void setSendMod(String sendMod) {this.sendMod = sendMod;}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> copyPaymentFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
result.put("contrId", this.getContrId());
result.put("guid", this.getGuid());
result.put("hash", this.getHash());
result.put("USEB2B", this.getUseB2B());
result.put("isMobile", this.getIsMobile());
result.put("protocol", this.getProtocol());
result.put("host", this.getHost());
result.put("port", this.getPort());
result.put("urlPay", this.getUrlPay());
result.put("url", this.getUrlPay());
result.put("smsCode", this.getSmsCode());
result.put("action", this.getAction());
result.put("sendMod", this.getSendMod());
result.put("type", this.getType());
result.put("phone", this.getPhone());
result.put("email", this.getEmail());

        return result;
    }
}
