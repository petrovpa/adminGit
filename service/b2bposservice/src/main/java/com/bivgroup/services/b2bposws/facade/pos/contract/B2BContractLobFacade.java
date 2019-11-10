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
 * Фасад для сущности B2BContractLob
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRLOB",idFieldName="CONTRLOBID")
@BOName("B2BContractLob")
public class B2BContractLobFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRLOBID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractLobCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractLobInsert", params);
        result.put("CONTRLOBID", params.get("CONTRLOBID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRLOBID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRLOBID"})
    public Map<String,Object> dsB2BContractLobInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractLobInsert", params);
        result.put("CONTRLOBID", params.get("CONTRLOBID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRLOBID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRLOBID"})
    public Map<String,Object> dsB2BContractLobUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractLobUpdate", params);
        result.put("CONTRLOBID", params.get("CONTRLOBID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRLOBID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRLOBID"})
    public Map<String,Object> dsB2BContractLobModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractLobUpdate", params);
        result.put("CONTRLOBID", params.get("CONTRLOBID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRLOBID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRLOBID"})
    public void dsB2BContractLobDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractLobDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRLOBID - ИД</LI>
     * <LI>PAGERAW - Данные страницы</LI>
     * <LI>PAGEROUTE - строчка роутинга страницы</LI>
     * <LI>PAGESYSNAME - Системное имя страницы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractLobBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractLobBrowseListByParam", "dsB2BContractLobBrowseListByParamCount", params);
        return result;
    }





}
