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
 * Фасад для сущности LossesProduct
 *
 * @author reson
 */
@IdGen(entityName="LOSS_PROD",idFieldName="PRODID")
@BOName("LossesProduct")
public class LossesProductFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME", "PRODKINDID"})
    public Map<String,Object> dsLossesProductCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductInsert", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID", "NAME", "PRODKINDID"})
    public Map<String,Object> dsLossesProductInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductInsert", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsLossesProductUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductUpdate", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsLossesProductModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductUpdate", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public void dsLossesProductDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesProductDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesProductBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesProductBrowseListByParam", "dsLossesProductBrowseListByParamCount", params);
        return result;
    }





}
