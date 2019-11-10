package ru.diasoft.services.bivsberlossws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "CheckPaymentStatusParams")
public class CheckPaymentStatusParams {
    @XmlElement(name = "payFactId")
    private Integer payFactId = null;

    @XmlElement(name = "sessionid")
    private String sessionId = null;

    public Integer getPayFactId() {
        return payFactId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
