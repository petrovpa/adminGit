package ru.diasoft.services.bivsberlossws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "CreatePaymentParams")
public class CreatePaymentParams {
    @XmlElement(name = "sessionid")
    private String sessionId = null;

    @XmlElement(name = "contrId")
    private Integer contrId = null;

    @XmlElement(name = "phone")
    private String phone = null;

    @XmlElement(name = "email")
    private String email = null;

    public String getSessionId() {
        return sessionId;
    }

    public Integer getContrId() {
        return contrId;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
