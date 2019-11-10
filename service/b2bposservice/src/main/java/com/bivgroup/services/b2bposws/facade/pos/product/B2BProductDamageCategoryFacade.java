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
 * Фасад для сущности B2BProductDamageCategory
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDAMAGECAT",idFieldName="PRODDAMAGECATID")
@BOName("B2BProductDamageCategory")
public class B2BProductDamageCategoryFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDamageCategoryCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDamageCategoryInsert", params);
        result.put("PRODDAMAGECATID", params.get("PRODDAMAGECATID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATID"})
    public Map<String,Object> dsB2BProductDamageCategoryInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDamageCategoryInsert", params);
        result.put("PRODDAMAGECATID", params.get("PRODDAMAGECATID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATID"})
    public Map<String,Object> dsB2BProductDamageCategoryUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDamageCategoryUpdate", params);
        result.put("PRODDAMAGECATID", params.get("PRODDAMAGECATID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATID"})
    public Map<String,Object> dsB2BProductDamageCategoryModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDamageCategoryUpdate", params);
        result.put("PRODDAMAGECATID", params.get("PRODDAMAGECATID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATID"})
    public void dsB2BProductDamageCategoryDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDamageCategoryDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONDITION - Условия и ограничения (на некоем МетаЯзыке)</LI>
     * <LI>DAMAGECATID - ИД категории ущерба</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба</LI>
     * <LI>ISNEEDCHECKDOC - Флаг необходимости ручной проверки прикрепленных документов</LI>
     * <LI>ISNEEDUW - Флаг необходимости ручного рассмотрения заявления на выплату по СС</LI>
     * <LI>ISRZUAUTOSET - Флаг автоматической установки первого РЗУ</LI>
     * <LI>MINRZUVALUE - Минимальная сумма первого РЗУ</LI>
     * <LI>NAME - Наименование категории ущерба</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODINSEVENTID - ИД вида страхового события по продукту</LI>
     * <LI>PRODSTRUCTOBJID - ИД объекта в структуре продукта</LI>
     * <LI>PRODSTRUCTRISKID - ИД риска в структуре продукта</LI>
     * <LI>PRODSTRUCTTOBJID - ИД группы объектов в структуре продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование категории ущерба</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDamageCategoryBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDamageCategoryBrowseListByParam", "dsB2BProductDamageCategoryBrowseListByParamCount", params);
        return result;
    }





}
