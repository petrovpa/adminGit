/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2bAddAgrCnt
 *
 * @author reson
 */
@IdGen(entityName="B2B_ADDAGRCNT",idFieldName="ADDAGRCNTID")
@BOName("B2bAddAgrCnt")
public class B2bAddAgrCntFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCNTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2bAddAgrCntCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2bAddAgrCntInsert", params);
        result.put("ADDAGRCNTID", params.get("ADDAGRCNTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCNTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCNTID"})
    public Map<String,Object> dsB2bAddAgrCntInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2bAddAgrCntInsert", params);
        result.put("ADDAGRCNTID", params.get("ADDAGRCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCNTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCNTID"})
    public Map<String,Object> dsB2bAddAgrCntUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2bAddAgrCntUpdate", params);
        result.put("ADDAGRCNTID", params.get("ADDAGRCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCNTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCNTID"})
    public Map<String,Object> dsB2bAddAgrCntModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2bAddAgrCntUpdate", params);
        result.put("ADDAGRCNTID", params.get("ADDAGRCNTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCNTID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCNTID"})
    public void dsB2bAddAgrCntDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2bAddAgrCntDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - ИД причины изменения</LI>
     * <LI>ADDAGRCNTID - ИД</LI>
     * <LI>ADDAGRID - ИД заголовка заявки на допс</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2bAddAgrCntBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2bAddAgrCntBrowseListByParam", "dsB2bAddAgrCntBrowseListByParamCount", params);
        return result;
    }





}
