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
 * Фасад для сущности B2BDamageCategory
 *
 * @author reson
 */
@IdGen(entityName="B2B_DAMAGECAT",idFieldName="DAMAGECATID")
@BOName("B2BDamageCategory")
public class B2BDamageCategoryFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BDamageCategoryCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BDamageCategoryInsert", params);
        result.put("DAMAGECATID", params.get("DAMAGECATID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DAMAGECATID"})
    public Map<String,Object> dsB2BDamageCategoryInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BDamageCategoryInsert", params);
        result.put("DAMAGECATID", params.get("DAMAGECATID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DAMAGECATID"})
    public Map<String,Object> dsB2BDamageCategoryUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BDamageCategoryUpdate", params);
        result.put("DAMAGECATID", params.get("DAMAGECATID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DAMAGECATID"})
    public Map<String,Object> dsB2BDamageCategoryModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BDamageCategoryUpdate", params);
        result.put("DAMAGECATID", params.get("DAMAGECATID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"DAMAGECATID"})
    public void dsB2BDamageCategoryDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BDamageCategoryDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DETAILHBDATAVERID - ИД версии справочника с детализацией</LI>
     * <LI>EXTERNALCODE - Внешний код</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>INSEVENTID - ИД страхового события</LI>
     * <LI>JSPATH - Путь к странице</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>VALUEHBDATAVERID - ИД версии справочника с показателями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BDamageCategoryBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BDamageCategoryBrowseListByParam", "dsB2BDamageCategoryBrowseListByParamCount", params);
        return result;
    }





}
