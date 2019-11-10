/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BInvestBaseActive
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVBASEACTIVE",idFieldName="INVBASEACTIVEID")
@BOName("B2BInvestBaseActive")
public class B2BInvestBaseActiveFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestBaseActiveCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestBaseActiveInsert", params);
        result.put("INVBASEACTIVEID", params.get("INVBASEACTIVEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBASEACTIVEID"})
    public Map<String,Object> dsB2BInvestBaseActiveInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestBaseActiveInsert", params);
        result.put("INVBASEACTIVEID", params.get("INVBASEACTIVEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBASEACTIVEID"})
    public Map<String,Object> dsB2BInvestBaseActiveUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestBaseActiveUpdate", params);
        result.put("INVBASEACTIVEID", params.get("INVBASEACTIVEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBASEACTIVEID"})
    public Map<String,Object> dsB2BInvestBaseActiveModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestBaseActiveUpdate", params);
        result.put("INVBASEACTIVEID", params.get("INVBASEACTIVEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBASEACTIVEID"})
    public void dsB2BInvestBaseActiveDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestBaseActiveDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>ISNOTUSE - Флаг не используется</LI>
     * <LI>ISSEVERALTICKERS - Флаг  несколько тикеров</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestBaseActiveBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestBaseActiveBrowseListByParam", "dsB2BInvestBaseActiveBrowseListByParamCount", params);
        return result;
    }





}
