/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductProgram
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_PRODPROG",idFieldName="PRODPROGID")
@BOName("B2BProductProgram")
public class B2BProductProgramFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME", "PRODVERID", "SYSNAME"})
    public Map<String,Object> dsB2BProductProgramCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductProgramInsert", params);
        result.put("PRODPROGID", params.get("PRODPROGID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPROGID", "NAME", "PRODVERID", "SYSNAME"})
    public Map<String,Object> dsB2BProductProgramInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductProgramInsert", params);
        result.put("PRODPROGID", params.get("PRODPROGID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPROGID"})
    public Map<String,Object> dsB2BProductProgramUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductProgramUpdate", params);
        result.put("PRODPROGID", params.get("PRODPROGID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPROGID"})
    public Map<String,Object> dsB2BProductProgramModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductProgramUpdate", params);
        result.put("PRODPROGID", params.get("PRODPROGID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPROGID"})
    public void dsB2BProductProgramDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductProgramDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Дата окончания эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Дата начала эксплуатации</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODPROGID - ИД программы продукта</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>ISUSETCOND - Признак использования типовых условий</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>PRODCODE - Код продукта для формирования номера договора</LI>
     * <LI>PRODRULEID - ИД правила страхования</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PROGCODE - Код программы</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductProgramBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductProgramBrowseListByParam", "dsB2BProductProgramBrowseListByParamCount", params);
        return result;
    }





}
