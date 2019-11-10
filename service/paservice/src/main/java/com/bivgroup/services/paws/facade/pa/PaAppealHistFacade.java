/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.paws.facade.pa;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности PaAppealHist
 *
 * @author reson
 */
@BinaryFile(objTableName = "PA_APPEALHIST", objTablePKFieldName = "APPEALHISTID")
@IdGen(entityName="PA_APPEALHIST",idFieldName="APPEALHISTID")
@BOName("PaAppealHist")
public class PaAppealHistFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALHISTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaAppealHistCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaAppealHistInsert", params);
        result.put("APPEALHISTID", params.get("APPEALHISTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALHISTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALHISTID"})
    public Map<String,Object> dsPaAppealHistInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaAppealHistInsert", params);
        result.put("APPEALHISTID", params.get("APPEALHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALHISTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALHISTID"})
    public Map<String,Object> dsPaAppealHistUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaAppealHistUpdate", params);
        result.put("APPEALHISTID", params.get("APPEALHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALHISTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALHISTID"})
    public Map<String,Object> dsPaAppealHistModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaAppealHistUpdate", params);
        result.put("APPEALHISTID", params.get("APPEALHISTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALHISTID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALHISTID"})
    public void dsPaAppealHistDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPaAppealHistDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaAppealHistBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsPaAppealHistBrowseListByParam", "dsPaAppealHistBrowseListByParamCount", params);
        return result;
    }





}
