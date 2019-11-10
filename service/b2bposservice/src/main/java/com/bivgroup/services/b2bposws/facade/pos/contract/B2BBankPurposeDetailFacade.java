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
 * Фасад для сущности B2BBankPurposeDetail
 *
 * @author reson
 */
@IdGen(entityName="B2B_BANKPURPOSEDETAIL",idFieldName="BANKPURPOSEDETAILID")
@BOName("B2BBankPurposeDetail")
public class B2BBankPurposeDetailFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankPurposeDetailCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankPurposeDetailInsert", params);
        result.put("BANKPURPOSEDETAILID", params.get("BANKPURPOSEDETAILID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILID"})
    public Map<String,Object> dsB2BBankPurposeDetailInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankPurposeDetailInsert", params);
        result.put("BANKPURPOSEDETAILID", params.get("BANKPURPOSEDETAILID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILID"})
    public Map<String,Object> dsB2BBankPurposeDetailUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankPurposeDetailUpdate", params);
        result.put("BANKPURPOSEDETAILID", params.get("BANKPURPOSEDETAILID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILID"})
    public Map<String,Object> dsB2BBankPurposeDetailModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankPurposeDetailUpdate", params);
        result.put("BANKPURPOSEDETAILID", params.get("BANKPURPOSEDETAILID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKPURPOSEDETAILID"})
    public void dsB2BBankPurposeDetailDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankPurposeDetailDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств</LI>
     * <LI>BANKPURPOSEDETAILID - ИД детализации назначения платежа</LI>
     * <LI>NUM - Порядковый номер детализации назначения</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankPurposeDetailBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankPurposeDetailBrowseListByParam", "dsB2BBankPurposeDetailBrowseListByParamCount", params);
        return result;
    }





}
