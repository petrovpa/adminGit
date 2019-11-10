/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.kladr.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
public class B2BKLADRBaseFacade extends BaseFacade {

    //<editor-fold defaultstate="collapsed" desc="из B2BBaseFacade">
    
    protected Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        if (isVerboseLog) {
            return callServiceLogged(serviceName, methodName, params, login, password);
        } else {
            return this.callService(serviceName, methodName, params, login, password);
        }
    }

    protected Map<String, Object> callServiceLogged(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + callResult.toString() + "\n");
        // возврат результата
        return callResult;
    }
    
    protected Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password, String keyName) throws Exception {
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.callService(serviceName, methodName, params, isVerboseLog, login, password);
        if (rawResult != null) {
            Object oneValue = rawResult.get(keyName);
            if (oneValue != null) {
                return oneValue;
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        return null;        
    }    
    
    protected Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, String login, String password, String keyName) throws Exception {
        boolean isVerboseLog = false;
        return callServiceAndGetOneValue(serviceName, methodName, params, isVerboseLog, login, password, keyName);
    }

    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.get("Status") != null) && ("OK".equalsIgnoreCase(callResult.get("Status").toString()));
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="из ProductContractCustomFacade / B2BCustomFacade">
    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    /**
     * Вызывает обычную версию selectQuery, передавая в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName    имя фактического запроса
     * @param selectParams параметры выполнения запроса
     *
     * @return результат, аналогичный получаемому при вызове обычной версии
     *         selectQuery
     *
     * @throws Exception
     */
    protected Map<String, Object> selectQuery(String queryName, Map<String, Object> selectParams) throws Exception {
        return this.selectQuery(queryName, queryName + "Count", selectParams);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="созданы в B2BPrimaryActivityBaseFacade">
    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }
    
    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }
    
    protected Integer getIntegerParam(Object bean, Integer defaultValue) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return defaultValue;
        }
    }

    protected Integer getIntegerParam(Object bean) {
        return getIntegerParam(bean, 0);
    }
    
    protected boolean getBooleanParam(Object bean, Boolean defaultValue) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defaultValue;
        }
    }

    protected boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defaultValue) {
        boolean booleanParam = defaultValue;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defaultValue);
        }
        return booleanParam;
    }
    
    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, boolean isVerboseLog, String login, String password) throws Exception {
        return WsUtils.getListFromResultMap(this.callService(serviceName, methodName, params, isVerboseLog, login, password));
    }
    
    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        boolean isVerboseLog = false;
        return callServiceAndGetListFromResultMap(serviceName, methodName, params, isVerboseLog, login, password);
    }
    //</editor-fold>
    
}
