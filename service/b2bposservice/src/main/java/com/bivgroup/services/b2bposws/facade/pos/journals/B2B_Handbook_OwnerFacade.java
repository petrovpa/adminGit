/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.journals;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2B_Handbook_Owner
 *
 * @author reson
 */
@IdGen(entityName="B2B_HANDBOOK_OWNER",idFieldName="ID")
@BOName("B2B_Handbook_Owner")
public class B2B_Handbook_OwnerFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_Handbook_OwnerCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_Handbook_OwnerInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_OwnerInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_Handbook_OwnerInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_OwnerUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_Handbook_OwnerUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_OwnerModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_Handbook_OwnerUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsB2B_Handbook_OwnerDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_Handbook_OwnerDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - ИД</LI>
     * <LI>USERID - ссылка на объект учета «Пользователь системы»</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_Handbook_OwnerBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_Handbook_OwnerBrowseListByParam", "dsB2B_Handbook_OwnerBrowseListByParamCount", params);
        return result;
    }





}
