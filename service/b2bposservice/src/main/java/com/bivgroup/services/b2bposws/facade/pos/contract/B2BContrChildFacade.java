/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContrChild
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_CONTRCHILD",idFieldName="CONTRCHILDID")
@BOName("B2BContrChild")
public class B2BContrChildFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContrChildCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContrChildInsert", params);
        result.put("CONTRCHILDID", params.get("CONTRCHILDID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRCHILDID"})
    public Map<String,Object> dsB2BContrChildInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContrChildInsert", params);
        result.put("CONTRCHILDID", params.get("CONTRCHILDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRCHILDID"})
    public Map<String,Object> dsB2BContrChildUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContrChildUpdate", params);
        result.put("CONTRCHILDID", params.get("CONTRCHILDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRCHILDID"})
    public Map<String,Object> dsB2BContrChildModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContrChildUpdate", params);
        result.put("CONTRCHILDID", params.get("CONTRCHILDID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRCHILDID"})
    public void dsB2BContrChildDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContrChildDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHILDID - Ид подчиненного договора</LI>
     * <LI>CONTRCHILDID - Первичный ключ</LI>
     * <LI>CONTRID - Ид договора заголовка</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>UPDATEDATE - Дата редактирования запись</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContrChildBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContrChildBrowseListByParam", "dsB2BContrChildBrowseListByParamCount", params);
        return result;
    }





}
