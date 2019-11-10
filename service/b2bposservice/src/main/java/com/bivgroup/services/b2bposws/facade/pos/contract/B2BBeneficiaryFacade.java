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
 * Фасад для сущности B2BBeneficiary
 *
 * @author reson
 */
@IdGen(entityName="B2B_BENEFICIARY",idFieldName="BENEFICIARYID")
@BOName("B2BBeneficiary")
public class B2BBeneficiaryFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARYID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBeneficiaryCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBeneficiaryInsert", params);
        result.put("BENEFICIARYID", params.get("BENEFICIARYID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARYID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BENEFICIARYID"})
    public Map<String,Object> dsB2BBeneficiaryInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBeneficiaryInsert", params);
        result.put("BENEFICIARYID", params.get("BENEFICIARYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARYID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BENEFICIARYID"})
    public Map<String,Object> dsB2BBeneficiaryUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBeneficiaryUpdate", params);
        result.put("BENEFICIARYID", params.get("BENEFICIARYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARYID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BENEFICIARYID"})
    public Map<String,Object> dsB2BBeneficiaryModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBeneficiaryUpdate", params);
        result.put("BENEFICIARYID", params.get("BENEFICIARYID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARYID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BENEFICIARYID"})
    public void dsB2BBeneficiaryDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBeneficiaryDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - Ссылка на договор</LI>
     * <LI>BENEFICIARYID - ИД</LI>
     * <LI>INSCOVERID - Ссылка на страховое покрытие</LI>
     * <LI>MEMBERID - Связь с участником договора</LI>
     * <LI>PART - Доля</LI>
     * <LI>PARTICIPANTID - Ссылка на лицо CRM</LI>
     * <LI>TYPEID - Тип лица выгодопреобретателя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBeneficiaryBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBeneficiaryBrowseListByParam", "dsB2BBeneficiaryBrowseListByParamCount", params);
        return result;
    }





}
