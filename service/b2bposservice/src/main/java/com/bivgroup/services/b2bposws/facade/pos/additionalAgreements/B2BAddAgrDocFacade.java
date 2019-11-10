/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BAddAgrDoc
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "B2B_ADDAGRDOC", objTablePKFieldName = "ADDAGRDOCID")
@IdGen(entityName="B2B_ADDAGRDOC",idFieldName="ADDAGRDOCID")
@BOName("B2BAddAgrDoc")
public class B2BAddAgrDocFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrDocCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrDocInsert", params);
        result.put("ADDAGRDOCID", params.get("ADDAGRDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRDOCID"})
    public Map<String,Object> dsB2BAddAgrDocInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrDocInsert", params);
        result.put("ADDAGRDOCID", params.get("ADDAGRDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRDOCID"})
    public Map<String,Object> dsB2BAddAgrDocUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrDocUpdate", params);
        result.put("ADDAGRDOCID", params.get("ADDAGRDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRDOCID"})
    public Map<String,Object> dsB2BAddAgrDocModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrDocUpdate", params);
        result.put("ADDAGRDOCID", params.get("ADDAGRDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRDOCID"})
    public void dsB2BAddAgrDocDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BAddAgrDocDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrDocBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAddAgrDocBrowseListByParam", "dsB2BAddAgrDocBrowseListByParamCount", params);
        return result;
    }





}
