/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BBankStateDocument
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "B2B_BANKSTATEDOC", objTablePKFieldName = "BANKSTATEDOCID")
@IdGen(entityName="B2B_BANKSTATEDOC",idFieldName="BANKSTATEDOCID")
@BOName("B2BBankStateDocument")
public class B2BBankStateDocumentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateDocumentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateDocumentInsert", params);
        result.put("BANKSTATEDOCID", params.get("BANKSTATEDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEDOCID"})
    public Map<String,Object> dsB2BBankStateDocumentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateDocumentInsert", params);
        result.put("BANKSTATEDOCID", params.get("BANKSTATEDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEDOCID"})
    public Map<String,Object> dsB2BBankStateDocumentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateDocumentUpdate", params);
        result.put("BANKSTATEDOCID", params.get("BANKSTATEDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEDOCID"})
    public Map<String,Object> dsB2BBankStateDocumentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateDocumentUpdate", params);
        result.put("BANKSTATEDOCID", params.get("BANKSTATEDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEDOCID"})
    public void dsB2BBankStateDocumentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankStateDocumentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Создавший пользователь</LI>
     * <LI>BANKSTATEDOCID - ИД записи</LI>
     * <LI>PRODBINDOCID - ИД типа документа</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Обновивший пользователь</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateDocumentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankStateDocumentBrowseListByParam", "dsB2BBankStateDocumentBrowseListByParamCount", params);
        return result;
    }





}
