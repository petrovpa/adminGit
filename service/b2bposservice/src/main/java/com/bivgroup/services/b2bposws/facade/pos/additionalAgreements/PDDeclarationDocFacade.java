/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;


import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;


/**
 * Фасад для сущности B2BAddAgrDoc
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "PD_DECLARATIONDOC", objTablePKFieldName = "DECLARATIONDOCID")
@IdGen(entityName="PD_DECLARATIONDOC",idFieldName="DECLARATIONDOCID")
@State(idFieldName = "DECLARATIONDOCID", startStateName = "PD_DECLARATIONDOC_NEW", typeSysName = "PD_DECLARATIONDOC")
@BOName("PDDeclarationDoc")
public class PDDeclarationDocFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPDDeclarationDocCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPDDeclarationDocInsert", params);
        result.put("DECLARATIONDOCID", params.get("DECLARATIONDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DECLARATIONDOCID"})
    public Map<String,Object> dsPDDeclarationDocInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPDDeclarationDocInsert", params);
        result.put("DECLARATIONDOCID", params.get("DECLARATIONDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DECLARATIONDOCID"})
    public Map<String,Object> dsPDDeclarationDocUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPDDeclarationDocUpdate", params);
        result.put("DECLARATIONDOCID", params.get("DECLARATIONDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * <LI>ADDAGRID - Ид допса</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>PRODBINDOCID - Ид типа документа</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DECLARATIONDOCID"})
    public Map<String,Object> dsPDDeclarationDocModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPDDeclarationDocUpdate", params);
        result.put("DECLARATIONDOCID", params.get("DECLARATIONDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>DECLARATIONDOCID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"DECLARATIONDOCID"})
    public void dsPDDeclarationDocDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPDDeclarationDocDelete", params);
    }










}
