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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author averichevsm
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Obj")
public class Obj {

    @XmlElement(name = "obj")
    private Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Map<String, Object> copyObjFromEntityToMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("obj", this.getObj());
        return result;
    }
}
