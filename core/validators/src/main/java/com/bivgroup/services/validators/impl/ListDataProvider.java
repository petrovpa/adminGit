/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.List;
import java.util.Map;
import com.bivgroup.services.validators.interfaces.ValidationException;

/**
 *
 * @author reson
 */
public abstract class ListDataProvider extends BaseDataProvider {

    private String mainBeanListParamName = null;

    public ListDataProvider(Map<String, Object> metadata) {
        super(metadata);
    }

    protected List<Map<String, Object>> getFields() {
        return (List<Map<String, Object>>) this.getMetadata().get("FIELDS");
    }

    @Override
    public String getMainBeanListParamName() {
        if (null == mainBeanListParamName) {

            for (Map<String, Object> field : this.getFields()) {
                Integer isMainField = (Integer) field.get("ISMAINFIELD");
                if ((isMainField != null) && (isMainField != 0)) {
                    this.mainBeanListParamName = (String) field.get("NAME");
                    break;
                }
            }
        }
        return this.mainBeanListParamName;
    }
}
