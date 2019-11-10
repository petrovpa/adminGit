/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.facade;

import com.bivgroup.services.paws.system.Constants;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@BOName("PABase")
public class PABaseFacade extends BaseFacade {

    protected static final String THIS_SERVICE_NAME = Constants.PAWS;

    public List<Map<String, Object>> callServiceAndGetListFromResultMap(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        return WsUtils.getListFromResultMap(this.callService(serviceName, methodName, params, login, password));
    }

    /**
     * Вызывает обычную версию selectQuery, передавая в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName имя фактического запроса
     * @param selectParams параметры выолнения запроса
     *
     * @return результат, аналогичный получаемому при вызове обычной версии
     * selectQuery
     *
     * @throws Exception
     */
    public Map<String, Object> selectQuery(String queryName, Map<String, Object> selectParams) throws Exception {
        return this.selectQuery(queryName, queryName + "Count", selectParams);
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

    protected boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defVal;
        }
    }

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

    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    protected String removeStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.remove(keyName));
        }
        return stringParam;
    }

    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    protected boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defaultValue) {
        Boolean booleanParam = defaultValue;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defaultValue);
        }
        return booleanParam;
    }

    protected String getLogin(Map<String, Object> callParams) {
        return getStringParam(callParams, WsConstants.LOGIN);
    }

    protected String removeLogin(Map<String, Object> callParams) {
        return removeStringParam(callParams, WsConstants.LOGIN);
    }

    protected String getPassword(Map<String, Object> callParams) {
        return getStringParam(callParams, WsConstants.PASSWORD);
    }

    protected String removePassword(Map<String, Object> callParams) {
        return removeStringParam(callParams, WsConstants.PASSWORD);
    }

    // получение из списка последнего элемента у которого в attrName храниться значение attrValue
    public static Object getLastElementByAtrrValue(List<Map<String, Object>> list, String attrName, String attrValue) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Map element = list.get(i);
            if (attrValue.equalsIgnoreCase(element.get(attrName).toString())) {
                return element;
            }
        }
        return null;
    }

}
