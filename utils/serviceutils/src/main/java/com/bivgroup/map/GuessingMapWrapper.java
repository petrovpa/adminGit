package com.bivgroup.map;

import com.bivgroup.dateutil.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * Created by pascal on 26.12.16.
 */
public class GuessingMapWrapper implements MapWrapper {

    private Map innerMap;

    public GuessingMapWrapper(Map map) {
        this.innerMap = (map == null) ? new HashMap<>() : map;
    }

    @Override
    public boolean has(String key) {
        return innerMap.containsKey(key);
    }

    @Override
    public Map get() {
        return innerMap;
    }

    @Override
    public Object get(String key) {
        return innerMap.get(key);
    }

    @Override
    public String getString(String key) {
        return getString(key, "");
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
        return getDate(key, new Date(0));
    }

    @Override
    public Date getDate(String key, Date defaultVal) {
        Object val = innerMap.get(key);
        if (val == null || isBlank(val.toString())) {
            return defaultVal;
        }
        if (val instanceof Long) {
            return new Date((Long) val);
        }
        if (val instanceof Double) {
            return DateUtil.convertDate((Double) val);
        }
        return (Date) val;
    }

    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, FALSE);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultVal) {
        Object val = innerMap.get(key);
        if (val == null || isBlank(val.toString())) {
            return defaultVal;
        }
        return val.toString().matches("yes|YES|y|Y|1|true|TRUE");
    }

    @Override
    public List<Map> getList(String key) {
        Object list = innerMap.get(key);
        if (list != null) {
            return (List<Map>) list;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<MapWrapper> getListWrapper(String key) {
        List<MapWrapper> list = new ArrayList<>();
        for (Map map : getList(key)) {
            list.add(new GuessingMapWrapper(map));
        }
        return list;
    }

    @Override
    public Map getMap(String key) {
        Object map = innerMap.get(key);
        if (map != null) {
            return (Map) map;
        } else {
            return new HashMap();
        }
    }

    @Override
    public MapWrapper getMapWrapper(String key) {
        Object val = get().get(key);
        if (val == null) {
            return new GuessingMapWrapper(new HashMap());
        }
        if (val instanceof MapWrapper) {
            return (MapWrapper) val;
        }
        return new GuessingMapWrapper((Map) val);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

}
