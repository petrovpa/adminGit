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
 * Фасад для сущности B2BContractParticipantRole
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRPARTROLE",idFieldName="CONTRPARTROLEID")
@BOName("B2BContractParticipantRole")
public class B2BContractParticipantRoleFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractParticipantRoleCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractParticipantRoleInsert", params);
        result.put("CONTRPARTROLEID", params.get("CONTRPARTROLEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRPARTROLEID"})
    public Map<String,Object> dsB2BContractParticipantRoleInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractParticipantRoleInsert", params);
        result.put("CONTRPARTROLEID", params.get("CONTRPARTROLEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRPARTROLEID"})
    public Map<String,Object> dsB2BContractParticipantRoleUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractParticipantRoleUpdate", params);
        result.put("CONTRPARTROLEID", params.get("CONTRPARTROLEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRPARTROLEID"})
    public Map<String,Object> dsB2BContractParticipantRoleModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractParticipantRoleUpdate", params);
        result.put("CONTRPARTROLEID", params.get("CONTRPARTROLEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRPARTROLEID"})
    public void dsB2BContractParticipantRoleDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractParticipantRoleDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>EMPLOYEEID - ИД сотрудника</LI>
     * <LI>CONTRPARTROLEID - ИД роли</LI>
     * <LI>PARTPERCENT - Доля в процентах</LI>
     * <LI>PARTICIPANTID - ИД контрагента</LI>
     * <LI>ROLENAME - Наименование роли</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractParticipantRoleBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractParticipantRoleBrowseListByParam", "dsB2BContractParticipantRoleBrowseListByParamCount", params);
        return result;
    }





}
