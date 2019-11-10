/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesContr
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="LOSS_CONTR",idFieldName="CONTRID")
@BOName("LossesContr")
public class LossesContrFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesContrCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesContrInsert", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsLossesContrInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesContrInsert", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsLossesContrUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesContrUpdate", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsLossesContrModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesContrUpdate", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public void dsLossesContrDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesContrDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNUM - Номер договора</LI>
     * <LI>CNT_ID - ИД договора из IDS по которому возврат</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>DOCDATE - Дата договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODUCTSYSNAME - Системное наименование продукта</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID  - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesContrBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesContrBrowseListByParam", "dsLossesContrBrowseListByParamCount", params);
        return result;
    }





}
