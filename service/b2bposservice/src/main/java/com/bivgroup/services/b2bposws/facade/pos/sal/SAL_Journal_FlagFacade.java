/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.sal;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SAL_Journal_Flag
 *
 * @author reson
 */
@IdGen(entityName="SAL_JOURNAL_FLAG",idFieldName="ID")
@BOName("SAL_Journal_Flag")
public class SAL_Journal_FlagFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Journal_FlagCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Journal_FlagInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_FlagInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Journal_FlagInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_FlagUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Journal_FlagUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_FlagModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Journal_FlagUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsSAL_Journal_FlagDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_Journal_FlagDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FLAGEVENTID - Классификатор признаков события журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал аудита безопасности</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Journal_FlagBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_Journal_FlagBrowseListByParam", "dsSAL_Journal_FlagBrowseListByParamCount", params);
        return result;
    }





}
