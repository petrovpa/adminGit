/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesBinaryDocument
 *
 * @author reson
 */
@IdGen(entityName="LOSS_BINDOC",idFieldName="BINDOCID")
@BOName("LossesBinaryDocument")
public class LossesBinaryDocumentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesBinaryDocumentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesBinaryDocumentInsert", params);
        result.put("BINDOCID", params.get("BINDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCID"})
    public Map<String,Object> dsLossesBinaryDocumentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesBinaryDocumentInsert", params);
        result.put("BINDOCID", params.get("BINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCID"})
    public Map<String,Object> dsLossesBinaryDocumentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesBinaryDocumentUpdate", params);
        result.put("BINDOCID", params.get("BINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCID"})
    public Map<String,Object> dsLossesBinaryDocumentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesBinaryDocumentUpdate", params);
        result.put("BINDOCID", params.get("BINDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCID"})
    public void dsLossesBinaryDocumentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesBinaryDocumentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>NAME - Наименование документа</LI>
     * <LI>NOTE - Примечание</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesBinaryDocumentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesBinaryDocumentBrowseListByParam", "dsLossesBinaryDocumentBrowseListByParamCount", params);
        return result;
    }





}
