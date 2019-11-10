/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.flextera.insurance.mail.websms;

import java.util.HashMap;

/**
 *
 * @author kkulkov
 * @param <K>
 * @param <V>
 */
public class ConfigHashMap<K,V> extends HashMap {

    public String getParam(String key, String defaultValue) {
        Object value = super.get(key);
        if (null == value) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

}
