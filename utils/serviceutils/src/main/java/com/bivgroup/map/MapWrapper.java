package com.bivgroup.map;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pascal on 26.12.16.
 */
public interface MapWrapper {

    boolean has(String key);
    Map get();
    Object get(String key);
    String getString(String key);
    String getString(String key, String defaultVal);
    BigDecimal getBigDecimal(String key);
    BigDecimal getBigDecimal(String key, BigDecimal defaultVal);
    Double getDouble(String key);
    Double getDouble(String key, Double defaultVal);
    Long getLong(String key);
    Long getLong(String key, Long defaultVal);
    Date getDate(String key);
    Date getDate(String key, Date defaultVal);
    Boolean getBoolean(String key);
    Boolean getBoolean(String key, Boolean defaultVal);
    List<Map> getList(String key);
    List<MapWrapper> getListWrapper(String key);
    Map getMap(String key);
    MapWrapper getMapWrapper(String key);

}
