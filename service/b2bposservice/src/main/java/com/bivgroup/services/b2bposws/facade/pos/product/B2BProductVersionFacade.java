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
 * Фасад для сущности B2BProductVersion
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODVER",idFieldName="PRODVERID")
@BOName("B2BProductVersion")
public class B2BProductVersionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsB2BProductVersionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductVersionInsert", params);
        result.put("PRODVERID", params.get("PRODVERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID", "PRODID"})
    public Map<String,Object> dsB2BProductVersionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductVersionInsert", params);
        result.put("PRODVERID", params.get("PRODVERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductVersionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductVersionUpdate", params);
        result.put("PRODVERID", params.get("PRODVERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductVersionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductVersionUpdate", params);
        result.put("PRODVERID", params.get("PRODVERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public void dsB2BProductVersionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductVersionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>EXPLFINISHDATE - Конечная дата эксплуатации</LI>
     * <LI>EXPLSTARTDATE - Начальная дата эксплуатации</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>IMGPATH - Путь к картинке версии</LI>
     * <LI>JSPATH - Путь к стартовой JS</LI>
     * <LI>LOGOTYPE - Логотип</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCODE - Код продукта</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>STATEID - ИД состояния продукта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductVersionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductVersionBrowseListByParam", "dsB2BProductVersionBrowseListByParamCount", params);
        return result;
    }





}
