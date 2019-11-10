/*
* Copyright (c) Diasoft 2004-2011
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

/**
 * Фасад для сущности B2BProductDefaultValue
 *
 * @author aklunok
 */
@BOName("ProductDefaultValueCustomFacade")
public class B2BProductDefaultValueCustomFacade extends B2BBaseFacade {

    private static final String INSPRODUCTWS_SERVICE_NAME = Constants.INSPRODUCTWS;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    /**
     * Получить параметры по-умолчанию для продукта
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String, Object> dsB2BProductDefaultValueByProdConfId(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        // Get product default params
        Map<String, Object> qparams = new HashMap<String, Object>();
        qparams.put("PRODCONFID", Long.valueOf(params.get("PRODCONFID").toString()));
        Map<String, Object> qres = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByParam", qparams, login, password);
        List<Map<String, Object>> paramList = null;
        if ((qres != null) && (qres.containsKey(RESULT))) {
            paramList = (List<Map<String, Object>>) qres.get(RESULT);
            Map<String, Object> listParam = new HashMap<String, Object>();
            listParam.put("PRODDEFVALLIST", paramList);
            listParam.put("ReturnAsHashMap", "TRUE");
            result = dsB2BProductDefaultMapFromList(listParam);
        }       
        return result;
    }

    @WsMethod(requiredParams = {"PRODDEFVALLIST"})
    public Map<String, Object> dsB2BProductDefaultMapFromList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        // Get product default params
        List<Map<String, Object>> paramList = null;
        if ((params != null) && (params.containsKey("PRODDEFVALLIST"))) {
            paramList = (List<Map<String, Object>>) params.get("PRODDEFVALLIST");
        }
        if (paramList != null) {
            for (Map<String, Object> bean : paramList) {
                String name = bean.get("NAME").toString();
                if (bean.get("VALUE") != null) {
                    String strValue = bean.get("VALUE").toString();
                    String valueTypeName = bean.get("DEFTYPE").toString();
                    Object value = null;
                    try {
                        if (Float.class.getName().equals(valueTypeName)) {
                            value = Float.valueOf(strValue);
                        }
                        if (Long.class.getName().equals(valueTypeName)) {
                            value = Long.valueOf(strValue);
                        }
                        if (Double.class.getName().equals(valueTypeName)) {
                            value = Double.valueOf(strValue);
                        }
                        if (Integer.class.getName().equals(valueTypeName)) {
                            value = Integer.valueOf(strValue);
                        }
                        if (Boolean.class.getName().equals(valueTypeName)) {
                            value = Boolean.valueOf(strValue);
                        }
                        if (Date.class.getName().equals(valueTypeName)) {
                            value = Double.valueOf(strValue);
                        }
                    } catch (Exception ex) {
                        logger.warn(String.format("Error convert param [%s] value [%s] to type [%s]", name, strValue, valueTypeName), ex);
                        value = strValue;
                    }
                    if (null == value) {
                        value = strValue;
                    }
                    result.put(name, value);
                } else {
                    result.put(name, bean.get("VALUE"));

                }
            }
        }
        return result;
    }

    /**
     * Сохранить список значений продукта
     *
     * @author vkonovalova
     * @param params
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDEFVALLIST - Список мапов с параметрами продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID", "PRODDEFVALLIST"})
    public Map<String, Object> dsB2BProductDefaultValuesSave(Map<String, Object> params) throws Exception {

        logger.debug("dsB2BProductDefaultValuesSave start...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> valList = (ArrayList<Map<String, Object>>) params.get("PRODDEFVALLIST");
        Map<String, Object> qparams = new HashMap<String, Object>();
        Map<String, Object> qres;
        Integer prodDefValueId;

        for (Map<String, Object> defVal : valList) {
            logger.debug("PRODDEFVAL = " + defVal);
            qparams.put("PRODCONFID", params.get("PRODCONFID"));
            prodDefValueId = (Integer) defVal.get("PRODDEFVALID");
            String methodName;
            if (prodDefValueId == null) {
                qparams.put("DEFTYPE", defVal.get("DEFTYPE"));
                qparams.put("NAME", defVal.get("NAME"));
                qparams.put("VALUE", defVal.get("VALUE"));
                qparams.put("NOTE", defVal.get("NOTE"));
                methodName = "dsB2BProductDefaultValueCreate";
            } else {
                qparams.put("PRODDEFVALID", defVal.get("PRODDEFVALID"));
                qparams.put("VALUE", defVal.get("VALUE"));
                methodName = "dsB2BProductDefaultValueUpdate";
            }
            logger.debug("methodName = " + methodName);
            qres = this.callService(B2BPOSWS_SERVICE_NAME, methodName, qparams, login, password);
            logger.debug("qres = " + qres);
            qparams.clear();
        }

        logger.debug("dsB2BProductDefaultValuesSave finished.");

        return result;
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author nbrashko
     * @param params
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Описание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * <LI>NOTE - Описание</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductDefaultValueBrowseListByNameNote(Map<String, Object> params) throws Exception {
        if (params.get("NAME") != null) {
            if (params.get("NAME").toString().isEmpty()) {
                params.put("NAME", null);
            }
        }
        if (params.get("NOTE") != null) {
            if (params.get("NOTE").toString().isEmpty()) {
                params.put("NOTE", null);
            }
        }
        Map<String, Object> result = this.selectQuery("dsB2BProductDefaultValueBrowseListByNameNote", "dsB2BProductDefaultValueBrowseListByNameNoteCount", params);
        return result;
    }

    /** получение константы продукта по имени константы и сис. наименованию версии продукта */
    @WsMethod(requiredParams = {"NAME", PRODUCT_SYSNAME_PARAMNAME})
    public Map<String,Object> dsB2BProductDefaultValueGetByNameAndProductSysName(Map<String, Object> params) throws Exception {
        Map<String,Object> result = selectQuery("dsB2BProductDefaultValueGetByNameAndProductSysName", params);
        return result;
    }

}
