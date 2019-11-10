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
 * Фасад для сущности SAL_Terrorists_Tmp
 *
 * @author reson
 */
@IdGen(entityName="SAL_TERRORISTS_TMP",idFieldName="ID")
@BOName("SAL_Terrorists_Tmp")
public class SAL_Terrorists_TmpFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Terrorists_TmpCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Terrorists_TmpInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_TmpInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Terrorists_TmpInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_TmpUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Terrorists_TmpUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_TmpModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Terrorists_TmpUpdate", params);
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
    public void dsSAL_Terrorists_TmpDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_Terrorists_TmpDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESS - Адрес регистрации</LI>
     * <LI>ADDRESS2 - Адрес местонахождения</LI>
     * <LI>BIRTHDATE - Дата рождения</LI>
     * <LI>CONTRY - Страна регистрации</LI>
     * <LI>CONTRY2 - Страна местонахождения</LI>
     * <LI>DOCUMENTAUTHORITY - Кем выдан</LI>
     * <LI>DOCUMENTNUMBER - Номер документа</LI>
     * <LI>DOCUMENTSERIES - Серия документа</LI>
     * <LI>DOCUMENTTYPE - Код документа</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>IDROW - Ключ записи</LI>
     * <LI>INN - ИНН</LI>
     * <LI>NAME - Наименование лица</LI>
     * <LI>NAME2 - Дополнительно наименование</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PLACEOFBIRTH - Место рождения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Terrorists_TmpBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_Terrorists_TmpBrowseListByParam", "dsSAL_Terrorists_TmpBrowseListByParamCount", params);
        return result;
    }





}
