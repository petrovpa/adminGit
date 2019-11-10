package com.bivgroup.integrationservice.system;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Created by pascal on 26.12.16.
 */
public class MapWrapperImpl implements MapWrapper {

    private Map<String, Object> innerMap;

    public MapWrapperImpl(Map<String, Object> map) {
        this.innerMap = (map == null) ? new HashMap<>() : map;
    }

    @Override
    public boolean has(String key) {
        return innerMap.containsKey(key);
    }

    @Override
    public Map<String, Object> get() {
        return innerMap;
    }

    @Override
    public Object get(String key) {
        return innerMap.get(key);
    }

    @Override
    public String getString(String key) {
        return getString(key, EMPTY);
    }

    @Override
    public String getString(String key, String defaultVal) {
        Object val = innerMap.get(key);
        return (val == null) ? defaultVal : val.toString();
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultVal) {
        Object val = innerMap.get(key);
        if (val != null && isNotBlank(val.toString())) {
            return BigDecimal.valueOf(Double.valueOf(val.toString())).setScale(2, RoundingMode.HALF_UP);
        } else {
            return defaultVal;
        }
    }

    @Override
    public Double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    @Override
    public Double getDouble(String key, Double defaultVal) {
        Object val = innerMap.get(key);
        return (val == null) ? defaultVal : Double.valueOf(val.toString());
    }

    @Override
    public Long getLong(String key) {
        return getLong(key, 0L);
    }

    @Override
    public Long getLong(String key, Long defaultVal) {
        Object val = innerMap.get(key);
        return (val != null && isNotBlank(val.toString())) ? Long.valueOf(val.toString()) : defaultVal;
    }

    @Override
    public Date getDate(String key) {
        return getDate(key, new Date());
    }

    @Override
    public Date getDate(String key, Date defaultVal) {
        Object val = innerMap.get(key);
        return (val != null && isNotBlank(val.toString()) && (val instanceof Date)) ? (Date) val : defaultVal;
    }

    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, FALSE);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultVal) {
        Object val = innerMap.get(key);
        return (val != null && isNotBlank(val.toString())) ? Boolean.valueOf(val.toString()) : defaultVal;
    }

    @Override
    public List<Map<String, Object>> getList(String key) {
        Object list = innerMap.get(key);
        if (list != null) {
            return (List<Map<String, Object>>) list;
        } else {
            return emptyList();
        }
    }

    @Override
    public List<MapWrapper> getListWrapper(String key) {
        List<MapWrapper> list = new ArrayList<>();
        for (Map<String, Object> map : getList(key)) {
            list.add(new MapWrapperImpl(map));
        }
        return list;
    }

    @Override
    public Map<String, Object> getMap(String key) {
        Object map = innerMap.get(key);
        if (map != null) {
            return (Map<String, Object>) map;
        } else {
            return emptyMap();
        }
    }

    @Override
    public MapWrapper getMapWrapper(String key) {
        return new MapWrapperImpl(getMap(key));
    }

}
