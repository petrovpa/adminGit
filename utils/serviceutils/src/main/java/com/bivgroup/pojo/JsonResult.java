/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author reson
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "result")
public class JsonResult {
    public static final String RESULT_OK = "OK";
    public static final String RESULT_ERROR = "Error";
    public static final String RESULT_empty_LoginPass = "EMPTYLOGINPASS";
    
    @XmlElement(name = "resultStatus")
    private String resultStatus = null;
    @XmlElement(name = "resultJson")
    private String resultJson = null;

    /**
     * @return the resultStatus
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /**
     * @param resultStatus the resultStatus to set
     */
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    /**
     * @return the resultJson
     */
    public String getResultJson() {
        return resultJson;
    }

    /**
     * @param resultJson the resultJson to set
     */
    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}
