/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractSection
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRSECTION",idFieldName="CONTRSECTIONID")
@BOName("B2BContractSection")
public class B2BContractSectionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BContractSectionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractSectionInsert", params);
        result.put("CONTRSECTIONID", params.get("CONTRSECTIONID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "CONTRSECTIONID"})
    public Map<String,Object> dsB2BContractSectionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractSectionInsert", params);
        result.put("CONTRSECTIONID", params.get("CONTRSECTIONID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSECTIONID"})
    public Map<String,Object> dsB2BContractSectionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractSectionUpdate", params);
        result.put("CONTRSECTIONID", params.get("CONTRSECTIONID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSECTIONID"})
    public Map<String,Object> dsB2BContractSectionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractSectionUpdate", params);
        result.put("CONTRSECTIONID", params.get("CONTRSECTIONID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSECTIONID"})
    public void dsB2BContractSectionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractSectionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>FINISHDATE - Конечная дата действия</LI>
     * <LI>CONTRSECTIONID - ИД записи</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractSectionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractSectionBrowseListByParam", "dsB2BContractSectionBrowseListByParamCount", params);
        return result;
    }





}
