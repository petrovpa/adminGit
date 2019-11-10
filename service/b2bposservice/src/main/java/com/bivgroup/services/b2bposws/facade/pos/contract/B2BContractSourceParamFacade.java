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
 * Фасад для сущности B2BContractSourceParam
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRSRCPARAM",idFieldName="CONTRSRCPARAMID")
@BOName("B2BContractSourceParam")
public class B2BContractSourceParamFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractSourceParamCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractSourceParamInsert", params);
        result.put("CONTRSRCPARAMID", params.get("CONTRSRCPARAMID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSRCPARAMID"})
    public Map<String,Object> dsB2BContractSourceParamInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractSourceParamInsert", params);
        result.put("CONTRSRCPARAMID", params.get("CONTRSRCPARAMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSRCPARAMID"})
    public Map<String,Object> dsB2BContractSourceParamUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractSourceParamUpdate", params);
        result.put("CONTRSRCPARAMID", params.get("CONTRSRCPARAMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSRCPARAMID"})
    public Map<String,Object> dsB2BContractSourceParamModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractSourceParamUpdate", params);
        result.put("CONTRSRCPARAMID", params.get("CONTRSRCPARAMID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRSRCPARAMID"})
    public void dsB2BContractSourceParamDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractSourceParamDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRSRCPARAMID - ИД записи</LI>
     * <LI>NAME - Наименование параметра источника</LI>
     * <LI>VALUE - Значение параметра источника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractSourceParamBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractSourceParamBrowseListByParam", "dsB2BContractSourceParamBrowseListByParamCount", params);
        return result;
    }





}
