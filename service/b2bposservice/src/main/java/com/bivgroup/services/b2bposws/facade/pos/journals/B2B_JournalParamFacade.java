/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.journals;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2B_JournalParam
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_JOURNALPARAM",idFieldName="ID")
@BOName("B2B_JournalParam")
public class B2B_JournalParamFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalParamCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalParamInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalParamInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalParamInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalParamUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalParamUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalParamModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalParamUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsB2B_JournalParamDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_JournalParamDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATATYPEID - содержит классификацию параметра по значению</LI>
     * <LI>DATAPROVIDERID - содержит ссылку на дата провайдер</LI>
     * <LI>HANDBOOKID - ссылка на объект учета «Классификатор видов справочника»</LI>
     * <LI>ID - Ид</LI>
     * <LI>ISCOMPLEX - содержит признак комплексного параметра</LI>
     * <LI>ISREQUIRED - признак обязательного заполнения параметра, без возможности исключения</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>KEYFIELD - Поле идентификатор для справочника</LI>
     * <LI>MAINPARAMID - ссылка на объект учета «Параметр журнала», используется для связи подчиненных параметров, которые выводиться при выполнении условия главного параметра</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>NAMEFIELD - поле наименование для справочника</LI>
     * <LI>NAMESPACE - Путь к параметру ограничения</LI>
     * <LI>NOTE - содержит текст подробного описания параметра</LI>
     * <LI>PARAMSHOWEXPR - содержит список параметров, которые связаны с текущим и выводиться при выполнении условия</LI>
     * <LI>PARENTID - ссылка на объект учета «Параметр журнала», используется для связи параметров, которые являются частью комплексного параметра</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер параметра</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLCOMPONENT - содержит URL на компоненту параметра</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalParamBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_JournalParamBrowseListByParam", "dsB2B_JournalParamBrowseListByParamCount", params);
        return result;
    }





}
